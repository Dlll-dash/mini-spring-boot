package com.mini.boot.demo;

import com.mini.boot.annotation.SpringBootApplication;
import com.mini.boot.context.annotation.AutoConfigurationImportSelector;
import com.mini.boot.demo.service.WelcomService;
import com.mini.boot.web.MiniServletWebContext;
import com.mini.spring.annotation.Import;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

@SpringBootApplication
public class BootDemo {
    public static void main(String[] args){
        MiniServletWebContext ctx = new MiniServletWebContext(BootDemo.class);
        ctx.refresh();
        System.out.println(Arrays.toString(ctx.getBeanDefinitionNames()));
        System.out.println((String) ctx.getBean("autoGreeting"));
        System.out.println(ctx.getBean(WelcomService.class).welcom());

        System.out.println(">>> 服务已起，3 秒窗口，去 curl");
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ctx.close();
        System.out.println(">>> 已 close");
        try (Socket s = new Socket("localhost", 8080)) {
            System.out.println(">>> waiting...");
        } catch (IOException e) {
            System.out.println(">>> 关掉了: " + e.getClass().getSimpleName());
        }
    }
}
