package com.mini.spring.fixture.core;

import com.mini.spring.annotation.Autowired;
import com.mini.spring.annotation.Component;

@Component
public class Alpha {

    @Autowired
    Beta beta;

    public Beta getBeta() {
        return beta;
    }
}
