package com.mini.spring.fixture.aop;

import com.mini.spring.annotation.Autowired;
import com.mini.spring.annotation.Component;
import com.mini.spring.annotation.Transactional;

/**
 * 与 OrderServiceImpl 互相当字段注入，制造循环依赖；
 * 两边都要 @Transactional，才能同时被代理 —— 用来验证"字段里拿到的是代理"。
 */
@Component
public class CheckoutImpl implements Checkout {

    @Autowired
    OrderService orderService;

    @Override
    @Transactional
    public String pay() {
        return "paid";
    }

    @Override
    public OrderService getOrderService() {
        return orderService;
    }
}
