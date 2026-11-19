package com.mini.spring.context;

public interface ApplicationEventMulticaster {

    void addApplicationListenerBean(String listenerBeanName);

    void multicastEvent(ApplicationEvent event);
}
