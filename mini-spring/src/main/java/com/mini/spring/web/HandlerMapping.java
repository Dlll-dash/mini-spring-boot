package com.mini.spring.web;

import com.mini.spring.BeanFactoryAware;
import com.mini.spring.MiniBeanFactory;
import com.mini.spring.annotation.AnnotationUtils;
import com.mini.spring.annotation.Controller;
import com.mini.spring.web.annotation.RequestMapping;
import com.mini.spring.web.annotation.RequestMethod;
import com.mini.spring.web.exception.RequestMethodNotSupportedException;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HandlerMapping implements BeanFactoryAware {

    private final Map<String, List<Handler>> handlers = new HashMap<>();

    @Override
    public void setBeanFactory(MiniBeanFactory factory) {
        for(String beanName : factory.getBeanDefinitionNames()){
            Class<?> beanType = factory.getBeanDefinition(beanName).getBeanType();
            if(beanType!=null && isHandler(beanType)) {
                //获取类级的前缀
                String prefix = pathOf(mappingOf(beanType));

                for (Method m : beanType.getDeclaredMethods()) {
                    //拿到带RequestMapping的注解（@GetMapping,@PostMapping...）,或者RequestMapping自己
                    Annotation requestMapping = mappingOf(m);
                    if(requestMapping==null)continue;
                    RequestMethod requestMethod;

                    if(requestMapping instanceof RequestMapping){
                        requestMethod = ((RequestMapping) requestMapping).method();
                    }else {
                        requestMethod = requestMapping.annotationType().getAnnotation(RequestMapping.class).method();
                    }
                    //获取Handler,加入Handler列表
                    Handler handler = new Handler(beanName, m, requestMethod);
                    List<Handler> handlerList = handlers.get(prefix + pathOf(requestMapping));
                    if(handlerList == null){
                        handlerList = new ArrayList<>();
                    }
                    handlerList.add(handler);
                    //加上前缀
                    handlers.put(prefix + pathOf(requestMapping), handlerList);

                }
            }
        }
    }

    /**
     * 搜索RequestMapping标记注解
     * @param el
     * @return
     */
    private Annotation mappingOf(AnnotatedElement el){
        for(Annotation ann : el.getAnnotations()){
            Class<?> type = ann.annotationType();
            if(type== RequestMapping.class|| AnnotationUtils.findAnnotation(type, RequestMapping.class)!=null){
                //如果是类级的直接返回@RequestMapping本身；是方法级的就返回原注释(@GetMapping,@PostMapping...)
                return ann;
            }
        }
        return null;
    }

    /**
     * 通过反射调用注解的方法，获取注解中的路径属性
     * @param requestMapping
     * @return
     */
    private String pathOf(Annotation requestMapping){
        if(requestMapping == null){
            return "";
        }
        try {
            return (String)requestMapping.annotationType()
                    .getDeclaredMethod("value").invoke(requestMapping);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据路径和请求方法获取Handler
     * @param path
     * @param requestMethod
     * @return
     */
    public Handler getHandler(String path,String requestMethod){
        List<Handler> handlerList = handlers.get(path);
        if(handlerList==null){
            return null;
        }
        for(Handler handler : handlerList){
            //遍历Handler列表，匹配请求方法
            if(String.valueOf(handler.getRequestMethod()).equals(requestMethod)){
                return handler;
            }
        }
        throw new RequestMethodNotSupportedException(requestMethod,path);

    }

    private boolean isHandler(Class<?> type){
        return AnnotationUtils.findAnnotation(type,Controller.class)!=null;
    }


}
