package com.mini.spring.demo;

import com.mini.spring.context.annotation.DeferredImportSelector;
import com.mini.spring.context.annotation.ImportSelector;

public class DemoSelector implements DeferredImportSelector {
    @Override
    public Class<?>[] selectImports(Class<?> importClass) {
        return new Class[]{
                AutoConfig.class
        };
    }
}
