package com.mini.boot.demo.Controller;

import com.mini.spring.annotation.Controller;
import com.mini.spring.web.annotation.GetMapping;
import com.mini.spring.web.annotation.RequestMapping;
import com.mini.spring.web.annotation.RequestParam;

@RequestMapping("/HelloController")
@Controller
public class HelloController {

    @GetMapping("/hello")
    public String hello(@RequestParam("x") String x){
        return "Hello mini SpringBoot from " + x;
    }

    @GetMapping("/add")
    public String add(@RequestParam("a") int a, @RequestParam("b") int b) {
        return String.valueOf(a + b);
    }
}
