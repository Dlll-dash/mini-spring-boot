package com.mini.spring.web;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class WebUtils {
    public static void write(HttpExchange exchange, int code, String body)throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        //写入响应
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(code, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
}
