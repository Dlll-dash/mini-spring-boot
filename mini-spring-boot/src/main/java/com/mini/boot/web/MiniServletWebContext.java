package com.mini.boot.web;

import com.mini.spring.BeanDefinition;
import com.mini.spring.MiniContext;
import com.mini.spring.web.DispatcherServlet;
import com.mini.spring.web.HandlerMapping;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MiniServletWebContext extends MiniContext {

    private static final String WEB_SERVER_START_STOP_NAME = "webServerStartStop";

    /**
     * 初始化容器
     *
     * @param configClass 运行主类
     */
    public MiniServletWebContext(Class<?> configClass) {
        super(configClass);
    }

    @Override
    protected void onRefresh() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080),0);

            getFactory().registerBeanDefinition(WEB_SERVER_START_STOP_NAME,
                    new BeanDefinition(WEB_SERVER_START_STOP_NAME, WebServerStartStopLifecycle.class,new Object[]{server}));
            //暂时手写
            getFactory().registerBeanDefinition("handlerMapping",new BeanDefinition("handlerMapping", HandlerMapping.class));
            server.createContext("/",new DispatcherServlet(getFactory()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
