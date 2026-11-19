package com.mini.spring.fixture.event;

import com.mini.spring.context.ApplicationEvent;

public class PongEvent extends ApplicationEvent {

    public PongEvent(Object source) {
        super(source);
    }
}
