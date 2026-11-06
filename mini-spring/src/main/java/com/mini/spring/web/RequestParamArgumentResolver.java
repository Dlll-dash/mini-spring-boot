package com.mini.spring.web;

import com.mini.spring.SimpleTypeConverter;
import com.mini.spring.web.annotation.RequestParam;
import com.mini.spring.web.exception.RequestParamException;
import com.sun.net.httpserver.HttpExchange;

import java.lang.reflect.Parameter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class RequestParamArgumentResolver implements ArgumentResolver{

    @Override
    public Object resolve(HttpExchange exchange, Parameter parameter){

        RequestParam ann = parameter.getAnnotation(RequestParam.class);
        //参数对应的query参数名
        String queryName = ann.value();
        if(queryName.isEmpty()){
            if(parameter.isNamePresent()){
                queryName = parameter.getName();
            }else{
                throw new RequestParamException("Name for argument of type ["
                        +parameter.getType()
                        + "] not specified, and parameter name information not available via reflection." +
                        " Ensure that the compiler uses the '-parameters' flag.");
            }
        }
        //获取query参数，形式："name=x&age=1"
        String query = exchange.getRequestURI().getRawQuery();
        if(query == null){
            throw new RequestParamException("Query params in URI ["+exchange.getRequestURI()
                    +"] is null");
        }
        //按“&”切割键值表达式
        String[] KVs = query.split("&");
        for(String kv : KVs){
            //按“=”切割键值,只按照第一个‘=’切割
            int idx = kv.indexOf('=');
            String key;
            String value;
            if(idx==-1){
                key=kv;
                value="";
            }else {
                key = kv.substring(0, idx);
                if(key.isEmpty()){
                    throw new RequestParamException("Query parameter name cannot be empty");
                }
                value = kv.substring(idx+1);
            }
            key = URLDecoder.decode(key, StandardCharsets.UTF_8);
            value = URLDecoder.decode(value,StandardCharsets.UTF_8);
            if(key.equals(queryName)){
                try {
                    return SimpleTypeConverter.convertIfNecessary(value, parameter.getType());
                } catch (RuntimeException e) {
                    throw new RequestParamException("Cannot convert [" + value + "] to " + parameter.getType().getSimpleName());
                }
            }
        }
        throw new RequestParamException("Cannot find any query param ["+queryName+"]: "+exchange.getRequestURI());
    }




}
