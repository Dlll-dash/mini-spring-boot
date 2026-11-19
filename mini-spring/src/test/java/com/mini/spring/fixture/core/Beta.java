package com.mini.spring.fixture.core;

import com.mini.spring.annotation.Autowired;
import com.mini.spring.annotation.Component;

@Component
public class Beta {

    @Autowired
    Alpha alpha;

    public Alpha getAlpha() {
        return alpha;
    }
}
