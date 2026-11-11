package com.mini.spring.web;

import com.mini.spring.web.converter.HttpMessageConverter;
import com.mini.spring.web.exception.HttpMediaTypeNotSupportedException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.List;

public class RequestBodyArgumentResolver implements ArgumentResolver{
    private final List<HttpMessageConverter<Object>> converters;

    RequestBodyArgumentResolver(List<HttpMessageConverter<Object>> converters){
        this.converters=converters;
    }

    @Override
    public Object resolve(HttpServletRequest req, Parameter parameter) {
        String contentType = req.getContentType();
        if(contentType==null){
            contentType = "application/octet-stream";
        }
        for(HttpMessageConverter<Object> converter:converters) {
            if (converter.canRead(parameter.getType(), contentType)) {
                try {
                    return converter.read(parameter.getType(), req.getInputStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new HttpMediaTypeNotSupportedException(contentType);
    }
}
