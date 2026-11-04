package com.mini.spring;

public interface BeanFactoryAware extends Aware {

    /**
     * 传递工厂容器
     * @param factory
     */
    void setBeanFactory(MiniBeanFactory factory);

}
