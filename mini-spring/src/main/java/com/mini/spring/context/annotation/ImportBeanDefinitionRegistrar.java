package com.mini.spring.context.annotation;

import com.mini.spring.beans.BeanDefinitionRegistry;

public interface ImportBeanDefinitionRegistrar {

    /**
     * 把注入的类加入容器
     * @param importingClass 发起导入的那个配置类（容器里那个），不是 registrar 自己
     * @param registry 配方册
     */
    default void registerBeanDefinition(Class<?>importingClass,BeanDefinitionRegistry registry){
    }

}
