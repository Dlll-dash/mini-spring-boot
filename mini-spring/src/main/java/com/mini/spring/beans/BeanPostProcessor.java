package com.mini.spring.beans;

public interface BeanPostProcessor {

    /**
     * 初始化前处理方法，@PostConstruct
     * @param bean
     * @param beanName
     * @return
     */
    default Object postProcessBeforeInitialization(Object bean,String beanName){
        return bean;
    }

    /**
     * 初始化后处理方法，代理类完成实现
     * @param bean
     * @param beanName
     * @return
     */
    default Object postProcessAfterInitialization(Object bean,String beanName){
        return bean;
    }

    /**
     * 从缓存中获取bean
     * @param bean
     * @param beanName
     * @return
     */
    default Object getEarlyBeanReference(Object bean,String beanName){
        return bean;
    }
}
