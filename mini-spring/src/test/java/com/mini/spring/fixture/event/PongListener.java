package com.mini.spring.fixture.event;

import com.mini.spring.context.ApplicationListener;
import com.mini.spring.annotation.Component;

@Component
public class PongListener implements ApplicationListener<PongEvent> {

    public static int count;

    @Override
    public void onApplicationEvent(PongEvent event) {
        count++;
    }
}
