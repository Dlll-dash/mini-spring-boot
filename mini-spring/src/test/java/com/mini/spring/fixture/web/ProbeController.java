package com.mini.spring.fixture.web;

import com.mini.spring.annotation.Controller;
import com.mini.spring.web.annotation.DeleteMapping;
import com.mini.spring.web.annotation.GetMapping;
import com.mini.spring.web.annotation.PathVariable;
import com.mini.spring.web.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class ProbeController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @DeleteMapping("/hello")
    public String delete() {
        return "deleted";
    }

    @GetMapping("/item/{id}")
    public String item(@PathVariable("id") String id) {
        return id;
    }
}
