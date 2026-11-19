package com.mini.spring.demo;

import com.mini.spring.context.ApplicationEvent;

public class OrderPlacedEvent extends ApplicationEvent {

    OrderPlacedEvent(OrderService orderService){
        super(orderService);
    }

}
