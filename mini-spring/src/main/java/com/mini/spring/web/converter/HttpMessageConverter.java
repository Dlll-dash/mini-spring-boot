package com.mini.spring.web.converter;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;

public interface HttpMessageConverter <T>{

    boolean canRead(Class<?> clazz, String type);

    T read(Class<? extends T> clazz, InputStream inputStream);

    boolean canWrite(Class<?> clazz,String type);

    void write(T value, HttpServletResponse res) throws IOException;

}
