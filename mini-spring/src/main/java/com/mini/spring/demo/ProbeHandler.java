package com.mini.spring.demo;

import com.mini.spring.annotation.Component;
import com.mini.spring.web.annotation.GetMapping;

@Component
public class ProbeHandler {
    @GetMapping("/probe")
    public void probe(){
    }
}
