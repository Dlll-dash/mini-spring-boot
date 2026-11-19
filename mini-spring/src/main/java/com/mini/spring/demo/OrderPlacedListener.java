package com.mini.spring.demo;

import com.mini.spring.context.ApplicationListener;
import com.mini.spring.annotation.Component;

@Component
public class OrderPlacedListener implements ApplicationListener<OrderPlacedEvent> {
    @Override
    public void onApplicationEvent(OrderPlacedEvent event) {
        System.out.println("[event]："+event.getClass().getSimpleName()+" ,listener@"+System.identityHashCode(this));
    }
}
