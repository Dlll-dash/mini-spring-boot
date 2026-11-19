package com.mini.spring.annotation;

import com.mini.spring.context.annotation.Condition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Conditional {
    Class<? extends Condition>[] value();
}
