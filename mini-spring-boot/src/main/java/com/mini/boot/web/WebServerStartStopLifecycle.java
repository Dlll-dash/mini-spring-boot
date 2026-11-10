package com.mini.boot.web;

import com.mini.boot.web.server.WebServer;
import com.mini.spring.Lifecycle;
import org.apache.catalina.LifecycleException;

public class WebServerStartStopLifecycle implements Lifecycle {
    private WebServer server;

    WebServerStartStopLifecycle(WebServer server){
        this.server=server;
    }

    @Override
    public void start() {
        try {
            server.start();
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop(){
        try {
            server.stop();
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
    }
}
