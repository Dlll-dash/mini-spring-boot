package com.mini.spring.fixture.event;

import com.mini.spring.context.ApplicationEvent;

public class PingEvent extends ApplicationEvent {

    public PingEvent(Object source) {
        super(source);
    }
}
