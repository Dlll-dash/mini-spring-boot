package com.mini.spring.context.annotation;

public interface ImportSelector {

    Class<?>[] selectImports(Class<?> importClass);

}
