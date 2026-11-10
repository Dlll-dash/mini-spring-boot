package com.mini.spring.web;

import com.mini.spring.web.annotation.RequestParam;
import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Parameter;

/**
 * 负责解析参数注解({@link RequestParam}等)。一个resolver负责解析一个参数
 */
public interface ArgumentResolver {
    Object resolve(HttpServletRequest req, Parameter parameter);
}
