package com.mini.spring.context;

import java.util.EventObject;

public abstract class ApplicationEvent extends EventObject {

    public ApplicationEvent(Object source) {
        super(source);
    }
}
