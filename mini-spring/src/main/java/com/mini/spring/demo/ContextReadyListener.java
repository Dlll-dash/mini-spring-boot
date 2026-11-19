package com.mini.spring.demo;

import com.mini.spring.context.ApplicationListener;
import com.mini.spring.context.ContextRefreshedEvent;
import com.mini.spring.annotation.Component;


@Component
public class ContextReadyListener implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("[event]："+event.getClass().getSimpleName()+" ,listener@"+System.identityHashCode(this));
    }
}
