package com.mini.spring.fixture.primary;

import com.mini.spring.annotation.Component;

@Component
public class BeltaGreeter implements Greeter{
    @Override
    public String greet() {
        return "Greet from Belta";
    }
}
