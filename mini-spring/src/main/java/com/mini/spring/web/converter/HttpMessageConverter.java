package com.mini.spring.web.converter;

import java.io.InputStream;

public interface HttpMessageConverter <T>{

    boolean canRead(Class<?> clzz, String type);

    T read(Class<? extends T> clazz, InputStream inputStream);

}
