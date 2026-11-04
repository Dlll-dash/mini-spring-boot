package com.mini.spring.web;

import com.mini.spring.MiniBeanFactory;
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


    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        if(handlerMapping==null){
            //懒加载
            handlerMapping = factory.getBean(HandlerMapping.class);
        }
        Handler handler = handlerMapping.getHandler(path);
        if(handler == null){
            String msg = "handler not found in path "+path;
            httpExchange.sendResponseHeaders(404,msg.getBytes().length);
            httpExchange.getResponseBody().write(msg.getBytes());
            httpExchange.close();
            return;
        }
        adapter.handle(handler,httpExchange);
    }
}
