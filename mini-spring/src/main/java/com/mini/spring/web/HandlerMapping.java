package com.mini.spring.web;

import com.mini.spring.BeanFactoryAware;
import com.mini.spring.MiniBeanFactory;
import com.mini.spring.annotation.AnnotationUtils;
import com.mini.spring.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class HandlerMapping implements BeanFactoryAware {

    private final Map<String, Handler> handlers = new HashMap<>();

    @Override
    public void setBeanFactory(MiniBeanFactory factory) {
        for(String beanName : factory.getBeanDefinitionNames()){
            Class<?> beanType = factory.getBeanDefinition(beanName).getBeanType();
            //获取类级的前缀
            String prefix=pathOf(mappingOf(beanType));

            for(Method m : beanType.getDeclaredMethods()){
                //拿到带RequestMapping的注解（@GetMapping,@PostMapping...）
                Annotation requestMapping = mappingOf(m);
                if(requestMapping != null){
                    //加上前缀
                    handlers.put(prefix+pathOf(requestMapping),new Handler(beanName,m));
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
     * 通过反射调用方法，获取注解中的路径
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

    public Handler getHandler(String path){
        return handlers.get(path);
    }

}
