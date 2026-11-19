package com.mini.spring.fixture.bean;

import com.mini.spring.annotation.Bean;
import com.mini.spring.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public StringBuilder builder() {
        return new StringBuilder("made by @Bean");
    }

    /** 参数按类型问容器要，配置类本身不作为参数名传进来 */
    @Bean
    public String greeting(StringBuilder builder) {
        return "hello from " + builder;
    }
}
