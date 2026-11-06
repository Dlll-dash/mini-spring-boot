package com.mini.spring.web;

import com.mini.spring.MiniBeanFactory;
import com.mini.spring.web.annotation.RequestParam;
import com.mini.spring.web.exception.RequestParamException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public class HandlerAdapter {

    private final MiniBeanFactory factory;

    private final Map<Class<? extends Annotation>,ArgumentResolver> resolvers = new HashMap<>(16);

    private static final Map<Class<? extends Annotation>,Class<?>>argumentTypeMap = Map.of(
            RequestParam.class,RequestParamArgumentResolver.class
            );

    HandlerAdapter(MiniBeanFactory factory){
        this.factory=factory;
        //初始化处理器
        for(Map.Entry<Class<? extends Annotation>,Class<?>> entry : argumentTypeMap.entrySet()){
            ArgumentResolver resolver = initiateResolver(entry.getValue());
            resolvers.put(entry.getKey(),resolver);
        }
    }

    /**
     * 负责统一执行接口请求方法，并将结果写入响应
     * @param handler
     * @param exchange
     * @throws IOException
     */
    public void handle(Handler handler, HttpExchange exchange)throws IOException {
        Object bean = factory.getBean(handler.getBeanName());
        Object[] args;
        try {
            args = resolveArgs(handler.getMethod(), exchange);
        }catch (RequestParamException e) {
            //解参出错，就是请求方的问题，响应码 400
            WebUtils.write(exchange,400,e.getMessage());
            return;
        }catch (RuntimeException e){
            //服务方的问题，响应码 500
            WebUtils.write(exchange,500,e.getMessage());
            return;
        }
        Object result;
        try {
            //传入参数，调用方法，获取结果
            result = handler.getMethod().invoke(bean, args);
        }catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            //调用方法出错，就是服务方的问题，响应码 500
            Throwable cause = e.getTargetException();
            String msg = cause.toString();
            WebUtils.write(exchange,500,msg);
            //重抛
            if(cause instanceof RuntimeException re)throw re;
            if(cause instanceof Error err)throw err;
            throw new RuntimeException(cause);
        }
        //写入响应
        WebUtils.write(exchange,200,String.valueOf(result));
    }

    private Object[] resolveArgs(Method method, HttpExchange exchange) {
        Parameter[] parameters = method.getParameters();
        //从请求中获取参数
        Object[] args = new Object[parameters.length];
        for(int i=0;i<parameters.length;i++){
            args[i] = resolveOne(exchange,method,parameters[i]);
        }
        return args;
    }

    private Object resolveOne(HttpExchange exchange, Method method, Parameter parameter){
        Annotation[] annotations = parameter.getAnnotations();
        for(Annotation ann : annotations){
            ArgumentResolver resolver = getResolver(ann.annotationType());
            if(resolver!=null){
                //多个可解析的注解，就只解析第一个。
                return resolver.resolve(exchange,parameter);
            }
        }
        throw new IllegalStateException("no resolver for parameter " + parameter.getName()
                + " of type [" + parameter.getType().getName() + "] on method " + method);
    }

    private ArgumentResolver getResolver(Class<? extends Annotation>type){
        return resolvers.get(type);
    }

    private static ArgumentResolver initiateResolver(Class<?> cls){
        try {
            return (ArgumentResolver) cls.getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


}
