package com.mini.spring.transaction;

import com.mini.spring.beans.BeanDefinition;
import com.mini.spring.beans.BeanDefinitionRegistry;
import com.mini.spring.context.annotation.ImportBeanDefinitionRegistrar;

public class TransactionConfigurationRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinition(Class<?> importingClass, BeanDefinitionRegistry registry) {
        EnableTransactionManagement enable = importingClass.getAnnotation(EnableTransactionManagement.class);
        if (enable == null) {
            throw new IllegalStateException(
                    "@EnableTransactionManagement is not present on importing class " + importingClass.getName());
        }

        String PROXY_CREATOR="autoProxyCreator";
        registry.registerBeanDefinition(PROXY_CREATOR,new BeanDefinition(PROXY_CREATOR,InternalAutoProxyCreator.class));

        String ADVISOR="transactionAdvisor";
        registry.registerBeanDefinition(ADVISOR,new BeanDefinition(ADVISOR,TransactionAdvisor.class,new Object[]{enable.order()}));


    }
}
