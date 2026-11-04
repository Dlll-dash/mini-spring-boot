package com.mini.spring.web;

import com.mini.spring.MiniBeanFactory;
import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

public class HandlerAdapter {

    private final MiniBeanFactory factory;

    HandlerAdapter(MiniBeanFactory factory){
        this.factory=factory;
    }

    public void handle(Handler handler, HttpExchange exchange)throws IOException {
        Object bean = factory.getBean(handler.getBeanName());
        try {
            Object result = handler.getMethod().invoke(bean);

            OutputStream responseBody = exchange.getResponseBody();
            //将结果转为字节数组，写入响应

                byte[] resBytes = String.valueOf(result).getBytes();
                //写入响应
                exchange.sendResponseHeaders(200,resBytes.length);
                responseBody.write(resBytes);
                exchange.close();


        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            //重抛
            Throwable cause = e.getTargetException();
            String msg = cause.getMessage();
            byte[] msgBytes = msg.getBytes();
            //返回异常响应
            exchange.sendResponseHeaders(500, msgBytes.length);
            exchange.getResponseBody().write(msgBytes);
            exchange.close();

            if(cause instanceof RuntimeException re)throw re;
            if(cause instanceof Error err)throw err;
            throw new RuntimeException(cause);
        }
    }

}
