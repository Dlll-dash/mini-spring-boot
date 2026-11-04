package com.mini.spring.web;

import java.lang.reflect.Method;
import java.util.Objects;

public class Handler {

    private final String beanName;

    private final Method method;

    Handler(String beanName, Method method){
        this.beanName = beanName;
        this.method = method;
    }

    public String getBeanName() {
        return beanName;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return "Handler{" +
                "beanName='" + beanName + '\'' +
                ", method=" + method +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Handler handler = (Handler) object;
        return Objects.equals(beanName, handler.beanName) && Objects.equals(method, handler.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beanName, method);
    }
}
