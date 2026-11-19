package com.mini.spring.beans;

@FunctionalInterface
public interface ObjectFactory<T> {
    /**
     * 工厂类生产方法
     * @return
     */
    T getObject();

}
