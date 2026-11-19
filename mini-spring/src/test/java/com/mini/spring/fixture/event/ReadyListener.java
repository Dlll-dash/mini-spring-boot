package com.mini.spring.fixture.event;

import com.mini.spring.context.ApplicationListener;
import com.mini.spring.context.ContextRefreshedEvent;
import com.mini.spring.annotation.Component;

@Component
public class ReadyListener implements ApplicationListener<ContextRefreshedEvent> {

    public static int count;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        count++;
    }
}
