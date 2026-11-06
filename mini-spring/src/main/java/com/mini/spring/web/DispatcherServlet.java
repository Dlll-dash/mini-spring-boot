package com.mini.spring.web;

import com.mini.spring.MiniBeanFactory;
import com.mini.spring.web.exception.RequestMethodNotSupportedException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class DispatcherServlet implements HttpHandler {
    private final MiniBeanFactory factory;
    private HandlerMapping handlerMapping;
    private final HandlerAdapter adapter;

    public DispatcherServlet(MiniBeanFactory factory) {
        this.factory = factory;
        this.adapter = new HandlerAdapter(factory);
    }


    /**
     * 负责接到请求后进行处理
     * @param httpExchange
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        //获取请求路径
        String path = httpExchange.getRequestURI().getPath();
        if(handlerMapping==null){
            //懒加载
            handlerMapping = factory.getBean(HandlerMapping.class);
        }
        //获取路径对应的方法 handler
        String requestMethod = httpExchange.getRequestMethod();
        Handler handler;
        try {
            handler = handlerMapping.getHandler(path, requestMethod);
        }catch (RequestMethodNotSupportedException e){
            WebUtils.write(httpExchange,e.getStatusCode(),e.getMessage());
            return;
        }
        if(handler == null){
            String msg = "handler not found in path: "+path;
            WebUtils.write(httpExchange,404,msg);
            return;
        }
        //执行路径对应的方法
        adapter.handle(handler,httpExchange);
    }
}
