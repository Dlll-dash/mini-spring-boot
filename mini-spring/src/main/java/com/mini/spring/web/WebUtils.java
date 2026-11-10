package com.mini.spring.web;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class WebUtils {
    public static void write(HttpServletResponse res, int code, String body)throws IOException {
        //写入响应
        res.setStatus(code);
        res.setContentType("text/plain; charset=UTF-8");
        res.getWriter().write(body);
    }
}
