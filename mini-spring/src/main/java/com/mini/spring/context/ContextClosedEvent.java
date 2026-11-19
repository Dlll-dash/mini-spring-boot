package com.mini.spring.context;

public class ContextClosedEvent extends ApplicationEvent{
    public ContextClosedEvent(Object source){
        super(source);
    }
}
