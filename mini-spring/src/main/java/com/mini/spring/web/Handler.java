package com.mini.spring.web;

import com.mini.spring.web.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 存储请求方法的一个数据载体
 * @param beanName
 * @param method
 * @param requestMethod
 */
public record Handler(String beanName, Method method, RequestMethod requestMethod) {

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Handler handler = (Handler) object;
        return Objects.equals(beanName, handler.beanName) && Objects.equals(method, handler.method) && requestMethod == handler.requestMethod;
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
