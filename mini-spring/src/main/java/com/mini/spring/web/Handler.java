package com.mini.spring.web;

import com.mini.spring.web.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.Objects;

public class Handler {

    private final String beanName;

    private final Method method;

    private final RequestMethod requestMethod;

    Handler(String beanName, Method method, RequestMethod requestMethod){
        this.beanName = beanName;
        this.method = method;
        this.requestMethod = requestMethod;
    }

    public String getBeanName() {
        return beanName;
    }

    public Method getMethod() {
        return method;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Handler handler = (Handler) object;
        return Objects.equals(beanName, handler.beanName) && Objects.equals(method, handler.method) && requestMethod == handler.requestMethod;
    }

    @Override
    public int hashCode() {
        return Objects.hash(beanName, method, requestMethod);
    }

    @Override
    public String toString() {
        return "Handler{" +
                "beanName='" + beanName + '\'' +
                ", method=" + method +
                ", requestMethod=" + requestMethod +
                '}';
    }

}
