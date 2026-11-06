package com.mini.spring.web;

import com.sun.net.httpserver.HttpExchange;

import java.lang.reflect.Parameter;

public interface ArgumentResolver {
    Object resolve(HttpExchange exchange, Parameter parameter);
}
