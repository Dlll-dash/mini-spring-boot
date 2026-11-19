package com.mini.spring.fixture.inject;

import com.mini.spring.annotation.Component;
import com.mini.spring.annotation.PostConstruct;

@Component
public class InitProbe {

    public static int calls;

    @PostConstruct
    public void init() {
        calls++;
    }
}
