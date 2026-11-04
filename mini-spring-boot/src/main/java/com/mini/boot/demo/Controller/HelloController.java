package com.mini.boot.demo.Controller;

import com.mini.spring.annotation.Controller;
import com.mini.spring.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("/hello")
    public String hello(){
        return "Hello mini SpringBoot";
    }
}
