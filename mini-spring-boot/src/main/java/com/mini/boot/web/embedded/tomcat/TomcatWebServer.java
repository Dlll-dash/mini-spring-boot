package com.mini.boot.web.embedded.tomcat;

import com.mini.boot.web.server.WebServer;
import jakarta.servlet.http.HttpServlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TomcatWebServer implements WebServer {

    private final Tomcat tomcat;

    public TomcatWebServer(int port, HttpServlet servlet){
        try {
            this.tomcat = new Tomcat();
            File baseDir = Files.createTempDirectory("mini-tomcat-base").toFile();
            tomcat.setBaseDir(baseDir.getAbsolutePath());
            tomcat.setPort(port);
            tomcat.getConnector();
            //contextPath为空 表示根应用  docBase是静态文件目录，必须存在，这里直接给的是临时目录
            Context context = tomcat.addContext("", baseDir.getAbsolutePath());
            Wrapper w = tomcat.addServlet(context, "dispatcherServlet", servlet);
            w.addMapping("/");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void start() throws LifecycleException {
        tomcat.start();
    }

    @Override
    public void stop() throws LifecycleException {
        tomcat.stop();
    }

    @Override
    public void destroy() throws LifecycleException {
        //必须先暂停再销毁
        stop();
        tomcat.destroy();
    }

    @Override
    public int getPort() {
        return tomcat.getConnector().getPort();
    }

}
