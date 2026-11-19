package com.mini.spring.demo;

import com.mini.spring.beans.BeanDefinitionRegistry;
import com.mini.spring.context.annotation.Condition;

import java.lang.reflect.AnnotatedElement;

public class FalseCondition implements Condition {
    @Override
    public boolean matches(AnnotatedElement source, BeanDefinitionRegistry registry) {
        return false;
    }
}
