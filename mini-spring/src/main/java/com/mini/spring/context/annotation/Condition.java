package com.mini.spring.context.annotation;

import com.mini.spring.beans.BeanDefinitionRegistry;

import java.lang.reflect.AnnotatedElement;

public interface Condition {
    boolean matches(AnnotatedElement source, BeanDefinitionRegistry registry);
}
