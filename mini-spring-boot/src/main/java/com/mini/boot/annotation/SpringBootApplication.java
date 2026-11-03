package com.mini.boot.annotation;

import com.mini.boot.context.annotation.AutoConfigurationExcludeFilter;
import com.mini.spring.annotation.ComponentScan;
import com.mini.spring.annotation.FilterType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ComponentScan(excludeFilters = {@ComponentScan.Filter(type = FilterType.CUSTOM,classes = AutoConfigurationExcludeFilter.class)})
@EnableAutoConfiguration
@SpringBootConfiguration
public @interface SpringBootApplication {
}
