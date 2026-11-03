package com.mini.spring.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentScan {

    String[] value() default {};

    Filter[] excludeFilters() default {};

    @interface Filter {
        FilterType type() default FilterType.ANNOTATION;

        Class<?>[] classes() default {};
    }


}
