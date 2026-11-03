package com.mini.boot.context.annotation;

import com.mini.boot.annotation.AutoConfiguration;
import com.mini.spring.annotation.AnnotationUtils;
import com.mini.spring.annotation.Configuration;
import com.mini.spring.annotation.TypeFilter;

import java.util.List;

public class AutoConfigurationExcludeFilter implements TypeFilter {

    //惰性缓存
    private List<String> autoConfigurations;

    @Override
    public boolean match(Class<?> candidate) {
        return isConfiguration(candidate) && isAutoConfiguration(candidate);
    }

    /**
     * 查找元注解
     * @param candidate
     * @return
     */
    private boolean isConfiguration(Class<?> candidate){
        return AnnotationUtils.findAnnotation(candidate, Configuration.class)!=null;
    }

    /**
     * 查找外部导入的自动配置
     * @param candidate
     * @return
     */
    private boolean isAutoConfiguration(Class<?> candidate){
        return getAutoConfigurations().contains(candidate.getName());
    }

    private List<String> getAutoConfigurations() {
        if(autoConfigurations==null) {
            ImportCandidates importCandidates = ImportCandidates.load(AutoConfiguration.class, getClass().getClassLoader());
            autoConfigurations = importCandidates.getCandidates();
        }
        return this.autoConfigurations;
    }
}
