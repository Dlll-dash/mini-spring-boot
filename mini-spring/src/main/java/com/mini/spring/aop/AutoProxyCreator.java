package com.mini.spring.aop;

import com.mini.spring.BeanFactoryAware;
import com.mini.spring.BeanPostProcessor;
import com.mini.spring.MiniBeanFactory;
import com.mini.spring.OrderComparator;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AutoProxyCreator implements BeanPostProcessor, BeanFactoryAware {

    private final Map<Object,Object> earlyBeanReferences = new ConcurrentHashMap<>(16);
    private MiniBeanFactory beanFactory;
    private List<Advisor> advisorList;


    public void setBeanFactory(MiniBeanFactory beanFactory){
        this.beanFactory=beanFactory;
    }

    public List<Advisor> getAdvisorList(){
        if(advisorList == null){
            advisorList = beanFactory.getBeanOfType(Advisor.class);
        }
        return advisorList;
    }

    /**
     * 返回原型或者代理的bean
     * @param bean
     * @param beanName
     * @return
     */
    @Override
    public Object getEarlyBeanReference(Object bean, String beanName) {
        earlyBeanReferences.put(beanName,bean);
        return wrapIfNecessary(bean,beanName);
    }

    /**
     * 后置代理方法，
     * 为了保证一致性，如果二级缓存已经有了代理，就不再生成新的代理
     * @param bean
     * @param beanName
     * @return
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if(earlyBeanReferences.remove(beanName)!=bean) {
            return wrapIfNecessary(bean, beanName);
        }
        return bean;
    }

    /**
     * 判断是否要生成代理类
     * @param bean
     * @param beanName
     * @return
     */
    private Object wrapIfNecessary(Object bean, String beanName){
        //避免在生产advisor时又进入是否代理的死循环，导致栈溢出
        if(bean instanceof Advisor){
            return bean;
        }

        if(shouldWrap(bean.getClass())){
            return createProxy(bean,beanName);
        }
        return bean;
    }

    /**
     * 只要有一个方法命中切点，就必须要代理
     * @param beanClass
     * @return
     */
    private boolean shouldWrap(Class<?>beanClass){
        for(Method method:beanClass.getDeclaredMethods()){
            if(getAdvisorsFor(method,beanClass).size()>0){
                return true;
            }
        }
        return false;
    }

    /**
     * JDK动态代理：按接口生成，所有接口方法从invoke进
     * 如果不是代理方法就直接调用原方法，如果是代理方法就进入拦截器方法。
     * @param bean
     * @param beanName
     * @return
     */
    private Object createProxy(Object bean,String beanName){
        Class<?> beanClass=bean.getClass();



        return Proxy.newProxyInstance(beanClass.getClassLoader(),beanClass.getInterfaces(),
                //handler函数，调用invoke时进入
                (proxy,method,args)->{

                    List<Advisor> chain=getAdvisorsFor(method,beanClass);
                    if(chain.size()==0){
                        return invokeTarget(bean,method,args);
                    }
                    //需要代理，传入bean，形成闭包
                    MethodInvocation invocation = ()-> invokeTarget(bean,method,args);

                    //从后往前包拦截器链
                    for(int i=chain.size()-1;i>=0;i--){
                        MethodInterceptor interceptor= chain.get(i).getAdvice();
                        MethodInvocation next = invocation;
                        //不断调用下一个拦截器逻辑
                        invocation = ()-> interceptor.invoke(next);
                    }
                    return invocation.proceed();
                });
    }

    /**
     * 获取到对应的advisors组成advice链
     * @param method
     * @param targetClass
     * @return
     */
    private List<Advisor> getAdvisorsFor(Method method, Class<?>targetClass){

        List<Advisor> result=new ArrayList<>();
        for(Advisor advisor:getAdvisorList()){
            if(advisor.getPointcut().matches(method, targetClass)){
                result.add(advisor);
            }
        }
        result.sort(new OrderComparator());
        return result;

    }

    /**
     * 调用目标方法，调用后进入handler函数
     * @param bean
     * @param method
     * @param args
     * @return
     */
    private Object invokeTarget(Object bean,Method method,Object[] args){
        try{
            return method.invoke(bean,args);
        }catch(InvocationTargetException e){
            //必须重抛，否则看不到报错信息
            Throwable cause = e.getTargetException();
            if(cause instanceof RuntimeException re)throw re;
            if (cause instanceof Error err) throw err;
            throw new RuntimeException(cause);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


}
