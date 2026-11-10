package com.mini.boot.web.server;

import org.apache.catalina.LifecycleException;

/**
 * 一个简单的接口，提供给Tomcat实现基本的服务功能
 */
public interface WebServer {

    void start() throws LifecycleException;

    void stop() throws LifecycleException;

    default void destroy() throws LifecycleException {stop();}

    int getPort();

}
