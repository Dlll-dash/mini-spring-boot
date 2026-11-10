package com.mini.boot.web;

import com.mini.boot.web.embedded.tomcat.TomcatWebServer;
import com.mini.boot.web.server.WebServer;
import com.mini.spring.BeanDefinition;
import com.mini.spring.MiniContext;
import com.mini.spring.web.DispatcherServlet;
import com.mini.spring.web.HandlerMapping;
import org.apache.catalina.LifecycleException;

public class MiniServletWebContext extends MiniContext {

    private static final String WEB_SERVER_START_STOP_NAME = "webServerStartStop";

    private WebServer server;

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

        //注册Tomcat
        WebServer server = new TomcatWebServer(8080,new DispatcherServlet(getFactory()));
        this.server=server;

        getFactory().registerBeanDefinition(WEB_SERVER_START_STOP_NAME,
                new BeanDefinition(WEB_SERVER_START_STOP_NAME, WebServerStartStopLifecycle.class,new Object[]{server}));
        //暂时手写注册
        getFactory().registerBeanDefinition("handlerMapping",new BeanDefinition("handlerMapping", HandlerMapping.class));

    }

    @Override
    protected void onClose() {
        try {
            //释放连接
            server.destroy();
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
    }
}
