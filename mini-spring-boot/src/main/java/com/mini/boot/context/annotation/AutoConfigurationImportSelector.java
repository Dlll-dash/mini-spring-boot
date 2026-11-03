package com.mini.boot.context.annotation;

import com.mini.boot.annotation.AutoConfiguration;
import com.mini.spring.DeferredImportSelector;

import java.util.ArrayList;
import java.util.List;

public class AutoConfigurationImportSelector implements DeferredImportSelector {
    @Override
    public Class<?>[] selectImports(Class<?> importClass) {
        ImportCandidates importCandidates = ImportCandidates.load(AutoConfiguration.class,getClass().getClassLoader());
        List<String> candidates = importCandidates.getCandidates();
        List<Class<?>>imports=new ArrayList<>();
        for(String candidate : candidates){
            try {
                //从类名直接加载进来
                Class<?> cls = Class.forName(candidate);
                imports.add(cls);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Class Not Found: ["+ candidate+"]");
            }
        }
        return imports.toArray(new Class[0]);
    }
}
