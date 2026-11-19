package com.mini.spring.beans;

import com.mini.spring.annotation.Component;
import com.mini.spring.annotation.PostConstruct;

import java.lang.reflect.Method;

/**
 * @PostConstruct 注解方法执行的后置处理器
 */
public class InitAnnotationBeanPostProcessor implements BeanPostProcessor{
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        for(Method m:bean.getClass().getDeclaredMethods()){
            if(m.isAnnotationPresent(PostConstruct.class)){
                try{
                    m.setAccessible(true);
                    m.invoke(bean);
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
        return bean;
    }
}
