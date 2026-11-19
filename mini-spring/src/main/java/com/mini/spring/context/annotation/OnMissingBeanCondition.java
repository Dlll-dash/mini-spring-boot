package com.mini.spring.context.annotation;

import com.mini.spring.beans.BeanDefinitionRegistry;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

public class OnMissingBeanCondition implements Condition{
    @Override
    public boolean matches(AnnotatedElement source, BeanDefinitionRegistry registry) {
        Class<?> target=(source instanceof Method  m) ? m.getReturnType() : (Class<?>)source;
        return !registry.contains(target);
    }
}
