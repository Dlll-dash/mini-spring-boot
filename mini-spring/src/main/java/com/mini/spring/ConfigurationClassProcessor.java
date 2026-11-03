package com.mini.spring;

import com.mini.spring.annotation.*;

import java.beans.Introspector;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class ConfigurationClassProcessor implements BeanFactoryPostProcessor{

    private final List<DeferredImportSelectorHolder>deferredImportSelectors = new ArrayList<>();

    /**
     * 扫包，初始化BeanFactory
     * @param registry
     */
    @Override
    public void postProcessBeanFactory(BeanDefinitionRegistry registry) {
        //扫描Import配置类会在运行时新增beanDefinition，需要用下标显式循环
        for(int i=0;i<registry.getBeanDefinitionNames().length;i++){
            //getBeanDefinitionNames()每次循环获取最新的快照，不能放在循环外
            String name=registry.getBeanDefinitionNames()[i];
            BeanDefinition bd=registry.getBeanDefinition(name);
            ComponentScan componentScan=AnnotationUtils.findAnnotation(bd.getBeanType(),ComponentScan.class);
            Set<Class<?>> imports=new LinkedHashSet<>();

            if(componentScan!=null){
                List<TypeFilter>filters=new ArrayList<>();
                for(ComponentScan.Filter filter : componentScan.excludeFilters()){
                    filters.addAll(getTypeFilter(filter.type(), filter.classes()));
                }
                String[] basePackages=componentScan.value();
                if(basePackages.length==0){
                    //默认配置类所在包
                    scan(registry,bd.getBeanType().getPackageName(),filters);
                }else{
                    //扫描注解标明的所有包
                    for(String basePackage:basePackages){
                        scan(registry,basePackage,filters);
                    }
                }
            }

            //收集Import导入类
            collectImports(bd.getBeanType(),imports,new HashSet<>());

            //处理Import导入类，包括ImportSelector
            processImports(bd.getBeanType(),imports,new HashSet<>(),registry);

            //处理@Bean注解导入
            processBeans(bd,registry);

            if(i == registry.getBeanDefinitionNames().length-1 && !deferredImportSelectors.isEmpty()){
                //最后处理deferred,
                for(DeferredImportSelectorHolder holder : deferredImportSelectors){
                    DeferredImportSelector importSelector = holder.getImportSelector();
                    Class<?>[] classes = importSelector.selectImports(holder.getConfigClass());
                    //再次处理
                    processImports(holder.getConfigClass(),new LinkedHashSet<>(Arrays.asList(classes)),new HashSet<>(),registry);
                }
                //处理完清空列表
                deferredImportSelectors.clear();
            }
        }

    }

    /**
     * 扫包
     * @param basePackage 根扫描包
     * @param filters 过滤器
     */
    private void scan(BeanDefinitionRegistry registry,String basePackage,List<TypeFilter> filters){
        String path=basePackage.replace('.','/');
        try {
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(path);
            if(!urls.hasMoreElements()){
                throw new IllegalStateException("Cannot find resources");
            }
            while(urls.hasMoreElements()){
                URL url=urls.nextElement();
                if(!url.getProtocol().equals("file"))continue;
                try {
                    //递归处理
                    walk(registry,new File(url.toURI()), basePackage,filters);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (URISyntaxException e){
                    throw new RuntimeException("URL Syntax Error");
                }
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }

    }

    /**
     * 递归扫包
     * @param dir
     * @param packageName
     * @param filters
     * @throws ClassNotFoundException
     */
    private void walk(BeanDefinitionRegistry registry,File dir, String packageName,List<TypeFilter> filters) throws ClassNotFoundException {
        File[] files=dir.listFiles();
        if(files==null)return;
        Arrays.sort(files);
        for(File f : files){
            String newPackageName=packageName+"."+f.getName();
            if(f.isDirectory()){
                walk(registry,f,newPackageName,filters);
            }else if(f.getName().endsWith(".class")){
                String className=newPackageName.replace(".class","");
                //这里在扫描时就加载类，而spring不会在扫描时加载,spring只会加载注解元数据
                //目前只是mini版，没有依赖，扫描时加载不会导致崩掉
                Class<?> c=Class.forName(className);

                //检查是否被排除
                if(isExcluded(c,filters)){
                    continue;
                }
                if(AnnotationUtils.findAnnotation(c,Component.class)!=null){
                    if(c.isAnnotationPresent(Conditional.class)&&shouldSkip(c.getAnnotation(Conditional.class),c,registry)){
                        continue;
                    }
                    //注册(暂时)
                    String beanName= Introspector.decapitalize(f.getName().replace(".class",""));
                    registry.registerBeanDefinition(beanName,new BeanDefinition(beanName,c));
                }
            }
        }
    }

    /**
     * 递归获取@Import注入的类
     * @param cls 当前正在查询的类
     * @param imports 注入类集合
     * @param visited 访问过的类集合
     */
    private void collectImports(Class<?> cls,Set<Class<?>>imports,Set<Class<?>> visited){
        if(visited.contains(cls)){
            return;
        }
        Annotation[] annotations = cls.getAnnotations();
        visited.add(cls);
        Import imp=cls.getAnnotation(Import.class);

        //递归元注解
        for(Annotation ann : annotations){
            if(ann.annotationType()!=Import.class) {
                collectImports(ann.annotationType(), imports, visited);
            }
        }

        //加入集合
        if(imp!=null){
            Class<?>[]classes=imp.value();
            imports.addAll(List.of(classes));
        }

    }

    /**
     * 递归处理Import,有selector就把所有选择的类加入容器，继续递归。
     * @param importClass 导入类，一路传下去就可以
     * @param importCandidates 候选集合
     * @param visited 访问过的集合
     * @param registry 注册容器
     */
    private void processImports(Class<?>importClass,Set<Class<?>>importCandidates,Set<Class<?>>visited,BeanDefinitionRegistry registry){

        for(Class<?> candidate:importCandidates){

            if(!visited.add(candidate)){
                //已经处理过的候选类就跳过
                continue;
            }
            try {
                if(DeferredImportSelector.class.isAssignableFrom(candidate)){
                    //如果是延迟selector
                    //实例化选择器
                    DeferredImportSelector selector = (DeferredImportSelector) candidate.getDeclaredConstructor().newInstance();
                    //加入deferred集合，最后处理
                    DeferredImportSelectorHolder holder = new DeferredImportSelectorHolder(importClass,selector);
                    deferredImportSelectors.add(holder);
                }
                else if(ImportSelector.class.isAssignableFrom(candidate)){
                    //如果是普通selector，就当场把它所带的候选者加入待处理队列中
                    ImportSelector selector = (ImportSelector) candidate.getDeclaredConstructor().newInstance();
                    Set<Class<?>>newImports=new LinkedHashSet<>(Arrays.asList(selector.selectImports(importClass)));
                    //继续递归处理
                    processImports(importClass,newImports,visited,registry);

                }else if(ImportBeanDefinitionRegistrar.class.isAssignableFrom(candidate)){

                    ImportBeanDefinitionRegistrar imp = (ImportBeanDefinitionRegistrar) candidate.getDeclaredConstructor().newInstance();
                    //传给 registrar 的是“导入者”，不是 registrar 自己
                    imp.registerBeanDefinition(importClass,registry);
                }else{
                    String beanName=Introspector.decapitalize(candidate.getSimpleName());
                    registry.registerBeanDefinition(beanName, new BeanDefinition(beanName, candidate));
                }
            }catch (NoSuchMethodException e){
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * 处理@Bean注解导入类
     * @param bd
     * @param registry
     */
    private void processBeans(BeanDefinition bd,BeanDefinitionRegistry registry){
        Conditional annotation = bd.getBeanType().getAnnotation(Conditional.class);
        //对于类上的条件注解判断
        if(annotation!=null&&shouldSkip(annotation,bd.getBeanType(),registry)){
            return;
        }
        Method[] methods=bd.getBeanType().getDeclaredMethods();
        for(Method method:methods){
            if(method.isAnnotationPresent(Bean.class)){
                //对于方法上的条件注解判断
                Conditional conditional = method.getAnnotation(Conditional.class);
                if(conditional!=null&&shouldSkip(conditional,method,registry)){
                    continue;
                }
                registerMethodBean(method,bd,registry);
            }
        }

    }

    /**
     * 注册@Bean类 方法声明的Bean
     * @param beanMethod @Bean类中的方法
     * @param beanFactory @Bean类，让注册类指向生产工厂bean
     * @param registry 注册表
     */
    private void registerMethodBean(Method beanMethod,BeanDefinition beanFactory,BeanDefinitionRegistry registry){
        Class<?>beanType=beanMethod.getReturnType();
        String methodName=beanMethod.getName();
        String beanFactoryName=beanFactory.getBeanName();

        BeanDefinition beanDefinition=new BeanDefinition();
        //表明来自哪个工厂类
        beanDefinition.setFactoryBeanName(beanFactoryName);
        //表明来自哪个方法
        beanDefinition.setFactoryMethodName(methodName);
        //方法名即为bean名称
        beanDefinition.setBeanName(methodName);
        beanDefinition.setBeanType(beanType);

        registry.registerBeanDefinition(methodName,beanDefinition);
    }

    /**
     * 判断@Condition条件是否满足
     * @param source 来源（方法/类）
     * @param registry
     * @return
     */
    private boolean shouldSkip(Conditional conditional, AnnotatedElement source, BeanDefinitionRegistry registry){
        Class<? extends Condition>[] classes = conditional.value();
        for (Class<? extends Condition> cls : classes) {
            try {
                Condition condition = cls.getDeclaredConstructor().newInstance();
                if (!condition.matches(source, registry)) return true;
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    /**
     * 内部类，存储配置类和选择器对。
     */
    private class DeferredImportSelectorHolder{

        private Class<?> configClass;

        private DeferredImportSelector importSelector;

        DeferredImportSelectorHolder(Class<?> configClass,DeferredImportSelector importSelector){
            this.configClass=configClass;
            this.importSelector=importSelector;
        }

        public Class<?> getConfigClass(){
            return configClass;
        }

        public DeferredImportSelector getImportSelector(){
            return importSelector;
        }
    }

    /**
     * 根据过滤器类型，将过滤器属性装入TypeFilter中，返回过滤器列表
     * @param type
     * @param filterAttributes
     * @return
     */
    private List<TypeFilter> getTypeFilter(FilterType type,Class<?>[] filterAttributes){
        List<TypeFilter> typeFilters = new ArrayList<>();
        for(Class<?> filterClass : filterAttributes){
            switch (type){
                case ANNOTATION -> {
                    if(!Annotation.class.isAssignableFrom(filterClass)){
                        throw new IllegalArgumentException("@ComponentScan ANNOTATION type filter requires an annotation type");
                    }
                    Class<Annotation> annotationType = (Class<Annotation>) filterClass;
                    typeFilters.add(new TypeFilter.AnnotationTypeFilter(annotationType));
                }
                case ASSIGNABLE_TYPE -> typeFilters.add(new TypeFilter.AssignableTypeFilter(filterClass));
                case CUSTOM -> {
                    if(!TypeFilter.class.isAssignableFrom(filterClass)){
                        throw new IllegalArgumentException("@ComponentScan CUSTOM type filter requires a TypeFilter implementation");
                    }
                    try {
                        TypeFilter typeFilter = (TypeFilter) filterClass.getDeclaredConstructor().newInstance();
                        typeFilters.add(typeFilter);
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }
                //暂时不会到默认分支
                default -> throw new IllegalArgumentException("Filter type not supported with Class value: " + type);
            }
        }
        return typeFilters;

    }

    /**
     * 判断是否被过滤
     * @param cls
     * @param filters
     * @return
     */
    private boolean isExcluded(Class<?>cls,List<TypeFilter>filters){
        for(TypeFilter filter : filters){
            if(filter.match(cls)){
                return true;
            }
        }
        return false;
    }
}


