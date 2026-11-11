package com.mini.spring.web;

import com.mini.spring.MiniBeanFactory;
import com.mini.spring.web.annotation.PathVariable;
import com.mini.spring.web.annotation.RequestBody;
import com.mini.spring.web.annotation.RequestParam;
import com.mini.spring.web.converter.HttpMessageConverter;
import com.mini.spring.web.converter.JacksonHttpMessageConverter;
import com.mini.spring.web.exception.HttpMediaTypeNotSupportedException;
import com.mini.spring.web.exception.HttpMessageNotReadableException;
import com.mini.spring.web.exception.RequestParamException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandlerAdapter {

    private final MiniBeanFactory factory;

    private final Map<Class<? extends Annotation>,ArgumentResolver> resolvers = new HashMap<>(16);

    private static final Map<Class<? extends Annotation>,Class<?>>argumentTypeMap = Map.of(
            RequestParam.class,RequestParamArgumentResolver.class,
            PathVariable.class, PathVariableArgumentResolver.class
            );

    private final List<HttpMessageConverter<Object>> converters = List.of(new JacksonHttpMessageConverter());

    HandlerAdapter(MiniBeanFactory factory){
        this.factory=factory;

        //手动注册一下RequestBody处理器，因为需要传入实例。
        resolvers.put(RequestBody.class,new RequestBodyArgumentResolver(converters));

        //初始化处理器
        for(Map.Entry<Class<? extends Annotation>,Class<?>> entry : argumentTypeMap.entrySet()){
            ArgumentResolver resolver = initiateResolver(entry.getValue());
            resolvers.put(entry.getKey(),resolver);
        }
    }

    /**
     * 负责统一执行接口请求方法，并将结果写入响应
     * @param handler
     * @param req
     * @param res
     * @throws IOException
     */
    public void handle(Handler handler, HttpServletRequest req, HttpServletResponse res)throws IOException {
        Object bean = factory.getBean(handler.getBeanName());
        Object[] args;
        try {
            args = resolveArgs(handler.getMethod(), req);

        }catch (HttpMediaTypeNotSupportedException e){
            //Content-type不支持，无法解析 RequestBody，响应码 415
            WebUtils.write(res,e.getStatusCode(),e.getMessage());
            return;

        } catch (HttpMessageNotReadableException e){
            //解析JSON时出错，无法读取，响应码 400
            WebUtils.write(res,400,e.getMessage());
            return;

        } catch (RequestParamException e) {
            //解参出错，就是请求方的问题，响应码 400
            WebUtils.write(res,400,e.getMessage());
            return;

        }catch (RuntimeException e){
            //服务方的问题，响应码 500
            WebUtils.write(res,500,e.getMessage());
            return;

        }
        Object result;
        try {
            //传入参数，调用方法，获取结果
            result = handler.getMethod().invoke(bean, args);
        }catch (IllegalAccessException e) {
            WebUtils.write(res,500,e.getMessage());
            return;
        } catch (InvocationTargetException e) {
            //调用方法出错，就是服务方的问题，响应码 500
            Throwable cause = e.getTargetException();
            String msg = cause.toString();
            WebUtils.write(res,500,msg);
            //重抛
            if(cause instanceof RuntimeException re)throw re;
            if(cause instanceof Error err)throw err;
            throw new RuntimeException(cause);
        }
        //写入响应
        WebUtils.write(res,200,String.valueOf(result));
    }

    private Object[] resolveArgs(Method method, HttpServletRequest req) {
        Parameter[] parameters = method.getParameters();
        //从请求中获取参数
        Object[] args = new Object[parameters.length];
        for(int i=0;i<parameters.length;i++){
            args[i] = resolveOne(req,method,parameters[i]);
        }
        return args;
    }

    private Object resolveOne(HttpServletRequest req, Method method, Parameter parameter){
        Annotation[] annotations = parameter.getAnnotations();
        for(Annotation ann : annotations){
            ArgumentResolver resolver = getResolver(ann.annotationType());
            if(resolver!=null){
                //多个可解析的注解，就只解析第一个。
                return resolver.resolve(req,parameter);
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
