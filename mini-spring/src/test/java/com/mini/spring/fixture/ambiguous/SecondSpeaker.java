package com.mini.spring.fixture.ambiguous;

import com.mini.spring.annotation.Component;

@Component
public class SecondSpeaker implements Speaker {

    @Override
    public String speak() {
        return "second";
    }
}
