package com.mini.spring.fixture.conditional;

import com.mini.spring.annotation.Bean;
import com.mini.spring.annotation.Conditional;
import com.mini.spring.annotation.Configuration;

@Configuration
public class ConditionalConfig {

    @Bean
    public String enabled() {
        return "enabled";
    }

    @Bean
    @Conditional(NeverCondition.class)
    public String disabled() {
        return "disabled";
    }
}
