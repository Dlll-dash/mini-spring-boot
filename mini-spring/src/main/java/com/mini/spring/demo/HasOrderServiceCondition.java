package com.mini.spring.demo;

import com.mini.spring.beans.BeanDefinitionRegistry;
import com.mini.spring.context.annotation.Condition;

import java.lang.reflect.AnnotatedElement;

public class HasOrderServiceCondition implements Condition {
    @Override
    public boolean matches(AnnotatedElement source, BeanDefinitionRegistry registry) {
        return registry.contains(OrderService.class);
    }
}
