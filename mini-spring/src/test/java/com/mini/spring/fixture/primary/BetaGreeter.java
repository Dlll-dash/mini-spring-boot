package com.mini.spring.fixture.primary;

import com.mini.spring.annotation.Component;
import com.mini.spring.annotation.Primary;

@Primary
@Component
public class BetaGreeter implements Greeter{
    @Override
    public String greet() {
        return "Greet from Belta";
    }
}
