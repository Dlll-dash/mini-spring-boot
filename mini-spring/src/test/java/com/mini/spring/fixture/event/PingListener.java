package com.mini.spring.fixture.event;

import com.mini.spring.context.ApplicationListener;
import com.mini.spring.annotation.Component;

@Component
public class PingListener implements ApplicationListener<PingEvent> {

    public static int count;

    @Override
    public void onApplicationEvent(PingEvent event) {
        count++;
    }
}
