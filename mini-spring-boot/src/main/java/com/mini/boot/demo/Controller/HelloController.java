package com.mini.boot.demo.Controller;

import com.mini.spring.annotation.Controller;
import com.mini.spring.web.annotation.*;

@RequestMapping("/HelloController")
@Controller
public class HelloController {

    @GetMapping("/hello")
    public String hello(@RequestParam String x){
        return "Hello mini SpringBoot from " + x;
    }

    @PostMapping("/hello")
    public String hello(){
        return "Hello from Post";
    }

    @GetMapping("/add")
    public String add(@RequestParam("a") int a, @RequestParam("b") int b) {
        return String.valueOf(a + b);
    }

    @GetMapping("/item/{id}")
    public String item(@PathVariable("id") String id){return id;}

    @GetMapping("/t/t")
    public String T1(){return "T1";}

    @GetMapping("/t/{x}")
    public String T2(){return "T2";}

    @GetMapping("/{y}/t")
    public String T3(){return "T3";}

    @GetMapping("/{x}/{y}")
    public String T4(){return "T4";}
}
