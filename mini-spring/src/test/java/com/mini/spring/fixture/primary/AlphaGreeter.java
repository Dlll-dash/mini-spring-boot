package com.mini.spring.fixture.primary;

import com.mini.spring.annotation.Component;

@Component
public class AlphaGreeter implements Greeter{
    @Override
    public String greet() {
        return "Greet from Alpha";
    }
}
