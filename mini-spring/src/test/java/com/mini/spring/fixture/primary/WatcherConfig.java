package com.mini.spring.fixture.primary;

import com.mini.spring.annotation.Bean;
import com.mini.spring.annotation.Configuration;
import com.mini.spring.annotation.Primary;

@Configuration
public class GreeterConfig {
    @Primary
    @Bean
    Watcher mainWatcher(){
        return ()-> "Watch by mainGreeter";
    }

    @Bean
    Watcher otherWatcher(){
        return ()->"Watch by otherWatcher";
    }
}
