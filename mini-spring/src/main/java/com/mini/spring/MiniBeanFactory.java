package com.mini.spring;

import com.mini.spring.annotation.Autowired;
import com.mini.spring.annotation.Bean;
import com.mini.spring.annotation.Value;
import com.mini.spring.exception.BeanAlreadyExistsException;
import com.mini.spring.exception.NoSuchBeanDefinitionException;
import com.mini.spring.exception.NoUniqueBeanDefinitionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MiniBeanFactory implements BeanDefinitionRegistry{

    private final Map<String,BeanDefinition> beanDefinitions = new ConcurrentHashMap<>(16);

    private final Map<String,ObjectFactory<?>>singletonFactories= new ConcurrentHashMap<>(16);

    private final Map<String,Object>earlySingletonObjects = new ConcurrentHashMap<>(16);

    private final Map<String,Object>singletonObjects=new ConcurrentHashMap<>(16);

    private final List<String> beanDefinitionNames=new ArrayList<>(16);

    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>(16);

    private final List<StringValueResolver> embeddedValueResolvers = new ArrayList<>(4);


    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        if(beanDefinitions.put(beanName, beanDefinition)==null) {
            beanDefinitionNames.add(beanName);
        }
    }

    @Override
    public BeanDefinition getBeanDefinition(String name) {
        return beanDefinitions.get(name);
    }

    public String[] getBeanDefinitionNames(){return beanDefinitionNames.toArray(new String[0]);}

    public void addEmbeddedValueResolver(StringValueResolver resolver){
        embeddedValueResolvers.add(resolver);
    }

    public String resolveEmbeddedValue(String value){
        for(StringValueResolver resolver:embeddedValueResolvers){
            value=resolver.resolvePlaceholders(value);
        }
        return value;
    }

    /**
     * 创建bean
     * @param beanName
     * @param beanType
     * @return
     * @param <T>
     */
    private <T> T createBean(String beanName,Class<T> beanType){
        try {
            //System.out.println(">>>Create Bean: "+beanName);
            BeanDefinition bd=getBeanDefinition(beanName);
            Object[] args=bd.getConstructArgumentValues();
            T obj = null;
            //选择进入有参或无参构造
            if(args!=null) {
                IllegalArgumentException last=null;
                //遍历所有构造器，匹配参数
                for(Constructor<?> constructor : beanType.getDeclaredConstructors()){
                    if(constructor.getParameterCount()!=args.length){
                        continue;
                    }
                    constructor.setAccessible(true);
                    try {
                        obj = (T)constructor.newInstance(args);
                        break;
                    }catch (IllegalArgumentException e){
                        last=e;
                    }
                }
                //没有匹配的则抛出异常
                if(obj==null){
                    throw new RuntimeException(
                            "Could not resolve matching constructor on bean class ["+beanName+":"+beanType.getSimpleName()+ "]",last
                    );
                }
            }else if(bd.getFactoryBeanName()!=null){
                //是@Bean方法构造的bean
                Object factoryBean = getBean(bd.getFactoryBeanName());
                Method[] methods=factoryBean.getClass().getDeclaredMethods();
                for(Method method:methods){
                    if(!isFactoryMethod(method) || !method.getName().equals(beanName)){
                        continue;
                    }
                    //获得Bean方法的形参类型列表
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    List<Object>parameters=new ArrayList<>();
                    for(Class<?> cls : parameterTypes){
                        //从容器中获取属性注入
                        Object parameter = getBean(cls);
                        parameters.add(parameter);
                    }
                    if(parameters.size()!=parameterTypes.length){
                        //如果参数数量与Bean方法参数数量不同的话就跳过
                        continue;
                    }
                    method.setAccessible(true);
                    //注入
                    obj = (T)method.invoke(factoryBean,parameters.toArray());
                }
            }
            else{
                //无参构造
                obj=(T) beanType.getDeclaredConstructor().newInstance();
            }
            T finalObj = obj;

            //lambda 实现ObjectFactory接口，重载getObject方法：返回经过PostProcessors处理过的代理
            singletonFactories.put(beanName,()-> getEarlyBeanReference(finalObj,beanName));

            for(Field f: beanType.getDeclaredFields()){
                //拿到被@Autowired注解的属性列表
                if(f.isAnnotationPresent(Autowired.class)){
                    f.setAccessible(true);
                    try {
                        //递归注入
                        f.set(obj, getBean(f.getType()));
                    }catch (IllegalAccessException e){
                        throw new RuntimeException(e);
                    }
                }else if(f.isAnnotationPresent(Value.class)){
                    Value annotation = f.getAnnotation(Value.class);
                    f.setAccessible(true);
                    String value = resolveEmbeddedValue(annotation.value());
                    Object object = SimpleTypeConverter.convertIfNecessary(value,f.getType());
                    f.set(obj,object);
                }
            }
            //注入完成通知aware
            if(obj instanceof BeanFactoryAware aware){
                aware.setBeanFactory(this);
            }
            //如果已经有代理则不会生效
            obj=applyBeanPostProcessorsBeforeInitialization(obj,beanName);
            obj=applyBeanPostProcessorsAfterInitialization(obj,beanName);

            //判断二级是否已有代理，有就替换。解决的是循环依赖中代理的一致性问题
            Object earlySingleton=earlySingletonObjects.get(beanName);
            if(earlySingleton!=null && obj==finalObj){
                obj=(T)earlySingleton;
            }

            //存入一级缓存时并检查是否已存在bean
            Object oldObject = singletonObjects.putIfAbsent(beanName,obj);
            if(oldObject!=null){
                throw new BeanAlreadyExistsException("there is already object "+beanName);
            }
            //移出三级缓存
            singletonFactories.remove(beanName);
            //移出二级缓存
            earlySingletonObjects.remove(beanName);

            return obj;
        }catch(InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }

    private boolean isFactoryMethod(Method method) {
        return method.isAnnotationPresent(Bean.class);
    }

    /**
     * 调用后置处理器的初始前方法
     * @param obj
     * @param beanName
     * @return
     * @param <T>
     */
    private <T> T applyBeanPostProcessorsBeforeInitialization(T obj, String beanName) {
        for(BeanPostProcessor pp:beanPostProcessors){
            obj =(T)pp.postProcessBeforeInitialization(obj,beanName);
        }
        return obj;
    }

    /**
     * 调用后置处理器的初始后方法
     * @param obj
     * @param beanName
     * @return
     * @param <T>
     */
    private <T> T applyBeanPostProcessorsAfterInitialization(T obj, String beanName) {
        //遍历处理器
        for(BeanPostProcessor pp:beanPostProcessors){
            obj =(T)pp.postProcessAfterInitialization(obj,beanName);
        }
        return obj;
    }

    /**
     * 通过bean名称获得实例 O(1)查询，没查到就新建
     * @param beanName
     * @return T
     * @param <T>
     */
    public <T> T getBean(String beanName){
        //先查一级缓存
        Object cached=singletonObjects.get(beanName);
        if(cached!=null){
            return (T)cached;
        }
        //二级缓存
        Object earlyCached=earlySingletonObjects.get(beanName);
        if(earlyCached!=null){
            return (T)earlyCached;
        }
        //三级缓存
        ObjectFactory<?> f=singletonFactories.get(beanName);
        if(f!=null){
            Object earlyBean = f.getObject();
            if(singletonFactories.remove(beanName)!=null){
                earlySingletonObjects.put(beanName,earlyBean);
                return (T)earlyBean;
            }
        }

        //拿配方新造一个
        BeanDefinition bd=beanDefinitions.get(beanName);
        if(bd==null){
            throw new NoSuchBeanDefinitionException("BeanDefinition Not Found");
        }
        return (T)createBean(beanName,bd.getBeanType());
    }

    /**
     * 通过类型获得bean实例 有O(N)的代价
     * @param type
     * @return
     * @param <T>
     */
    public <T> T getBean(Class<T> type){
        String matchedName=null;
        for(BeanDefinition bd:beanDefinitions.values()){
            Class<?> beanType=bd.getBeanType();
            if(type.isAssignableFrom(beanType)){
                if(matchedName!=null){
                    throw new NoUniqueBeanDefinitionException(
                            "expected single matching bean but found more than one: "
                                    + matchedName + ", " + bd.getBeanName());
                }
                matchedName=bd.getBeanName();
            }
        }
        if(matchedName==null){
            throw new NoSuchBeanDefinitionException("BeanDefinition Not Found");
        }
        return (T) getBean(matchedName);
    }

    /**
     * 根据类型获取所有所属bean
     * @param type
     * @return
     * @param <T>
     */
    public <T> List<T> getBeanOfType(Class<T> type){
        List<T> result = new ArrayList<>();
        for(String beanName : beanDefinitionNames){
            BeanDefinition beanDefinition = getBeanDefinition(beanName);
            if(type.isAssignableFrom(beanDefinition.getBeanType())){
                result.add(getBean(beanName));
            }
        }
        return result;
    }

    /**
     * 根据类型获取名称列表，不会创建bean
     * @param type
     * @return
     */
    public String[] getBeanNamesForType(Class<?> type){
        ArrayList<String>result=new ArrayList<>();
        for(String beanName:beanDefinitionNames){
            BeanDefinition bd=beanDefinitions.get(beanName);
            if(type.isAssignableFrom(bd.getBeanType())){
                result.add(beanName);
            }
        }
        return result.toArray(new String[0]);

    }


    /**
     * 根据类型判断容器中是否已存在bean
     * @param type
     * @return
     */
    @Override
    public boolean contains(Class<?> type) {
        return getBeanNamesForType(type).length>0;
    }

    /**
     * 获取代理
     * @param bean
     * @param beanName
     * @return
     */
    private Object getEarlyBeanReference(Object bean,String beanName){
        for(BeanPostProcessor pp:beanPostProcessors){
            bean = pp.getEarlyBeanReference(bean,beanName);
        }
        return bean;
    }

    /**
     * 将后置处理器加入处理器列表
     * @param pp
     */
    public void addBeanPostProcessor(BeanPostProcessor pp){
        beanPostProcessors.add(pp);
    }


}
