package com.mini.spring.demo;

import com.mini.spring.annotation.Autowired;
import com.mini.spring.annotation.Component;
import com.mini.spring.annotation.PostConstruct;
import com.mini.spring.annotation.Transactional;

public interface OrderService {

    String orderOf(long userId);

    String cancel(long userId);

    String describe(long userId);

    void placeOrder(String name);
}
