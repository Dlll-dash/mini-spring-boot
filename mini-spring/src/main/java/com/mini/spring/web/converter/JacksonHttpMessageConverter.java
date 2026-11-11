package com.mini.spring.web.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mini.spring.web.exception.HttpMessageNotReadableException;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class JacksonHttpMessageConverter extends AbstractHttpMessageConverter{
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JacksonHttpMessageConverter(){
        setSupportedMediaTypes(List.of("application/json","application/*+json"));
    }

    /**
     * 读取请求体
     * @param clazz-要读取的类 Class
     * @param inputStream-请求体的输入流
     * @return
     */
    @Override
    public Object read(Class<?> clazz, InputStream inputStream) {
        try {
            return objectMapper.readValue(inputStream, clazz);
        } catch (IOException e) {
            throw new HttpMessageNotReadableException("JSON parse error: " + e.getMessage(), e);
        }
    }


    @Override
    public void write(Object value, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        objectMapper.writeValue(res.getOutputStream(),value);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }
}
