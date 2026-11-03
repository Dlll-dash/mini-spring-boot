package com.mini.boot.demo.autoconfig;

import com.mini.spring.annotation.Bean;
import com.mini.spring.annotation.Configuration;

@Configuration
public class GreetingAutoConfiguration {
    @Bean
    public String autoGreeting(){
        return "Hello from autoGreeting";
    }
}
