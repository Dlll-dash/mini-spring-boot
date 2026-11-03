package com.mini.boot.annotation;

import com.mini.boot.context.annotation.AutoConfigurationImportSelector;
import com.mini.spring.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {
}
