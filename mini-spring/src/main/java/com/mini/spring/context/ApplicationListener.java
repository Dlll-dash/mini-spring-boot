package com.mini.spring.context;

public interface ApplicationListener<E extends ApplicationEvent> {

    void onApplicationEvent(E event);

}
