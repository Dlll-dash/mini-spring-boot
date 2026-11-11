package com.mini.spring.web.converter;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class StringHttpMessageConverter extends AbstractHttpMessageConverter{

    public StringHttpMessageConverter(){
        setSupportedMediaTypes(List.of("*/*","text/plain","text/html"));
    }

    @Override
    public Object read(Class clazz, InputStream inputStream) {
        Charset charset = StandardCharsets.UTF_8;
        try {
            //暂时先全部读取,不做范围判断
            byte[] bytes = inputStream.readAllBytes();
            return new String(bytes,charset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == String.class;
    }

    @Override
    public void write(Object value, HttpServletResponse res) throws IOException {
        res.setContentType("text/plain;charset=UTF-8");
        res.getWriter().write(String.valueOf(value));
    }

}
