package com.mini.spring.demo.controller;

import com.mini.spring.web.annotation.GetMapping;
import com.mini.spring.web.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello(){
        return "Hello mini SpringBoot";
    }
}
