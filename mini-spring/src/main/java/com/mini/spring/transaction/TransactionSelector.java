package com.mini.spring.transaction;

import com.mini.spring.context.annotation.ImportSelector;

public class TransactionSelector implements ImportSelector {
    @Override
    public Class<?>[] selectImports(Class<?> importClass) {
        return new Class[]{
                TransactionConfigurationRegistrar.class
        };
    }
}
