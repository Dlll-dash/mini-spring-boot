package com.mini.spring.fixture.aop;

import com.mini.spring.annotation.Autowired;
import com.mini.spring.annotation.Component;
import com.mini.spring.annotation.Transactional;

@Component
public class OrderServiceImpl implements OrderService {

    @Autowired
    Checkout checkout;

    @Override
    @Transactional
    public String place() {
        return "placed";
    }

    @Override
    @Transactional
    public String fail() {
        throw new IllegalStateException("boom");
    }

    @Override
    public String plain() {
        return "plain";
    }

    @Override
    public Checkout getCheckout() {
        return checkout;
    }
}
