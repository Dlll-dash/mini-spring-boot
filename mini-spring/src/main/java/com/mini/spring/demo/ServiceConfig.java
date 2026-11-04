package com.mini.spring.demo;

import com.mini.spring.OnMissingBeanCondition;
import com.mini.spring.annotation.*;

@Import(DemoSelector.class)
@Configuration
public class ServiceConfig {
    @Bean
    @Conditional(TrueCondition.class)
    public StringBuilder builder(){
        return new StringBuilder("made by @Bean");
    }

    @Bean
    @Conditional(HasOrderServiceCondition.class)
    public String greeting(OrderService orderService){
        return "hello from " + orderService.getClass().getSimpleName();
    }

    @Bean
    @Conditional(OnMissingBeanCondition.class)
    public Greeter fallbackGreeter(){
        return ()->{return "hello from @Bean fallback";};
    }
}
