package com.mini.spring.demo;

import com.mini.spring.annotation.Autowired;
import com.mini.spring.annotation.Component;
import com.mini.spring.annotation.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository{
    @Autowired
    private OrderService orderService;

    public String findName(long id) {
        return "user#" + id;
    }

    public String orderOf(long id){return orderService.orderOf(id);}
}
