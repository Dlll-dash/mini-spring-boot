package com.mini.spring.web.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mini.spring.web.exception.HttpMessageNotReadableException;

import java.io.IOException;
import java.io.InputStream;

public class JacksonHttpMessageConverter implements HttpMessageConverter<Object>{
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean canRead(Class<?> clzz, String type) {
        if(type!=null && type.contains("json")){
            return true;
        }
        return false;
    }

    @Override
    public Object read(Class<?> clazz, InputStream inputStream) {
        try {
            return objectMapper.readValue(inputStream, clazz);
        } catch (IOException e) {
            throw new HttpMessageNotReadableException("JSON parse error: " + e.getMessage(), e);
        }
    }
}
