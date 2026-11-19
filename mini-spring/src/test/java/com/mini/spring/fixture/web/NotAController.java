package com.mini.spring.fixture.web;

import com.mini.spring.annotation.Component;
import com.mini.spring.web.annotation.GetMapping;

/**
 * 有 @GetMapping 但没有 @Controller：映射表里不应该出现它。
 */
@Component
public class NotAController {

    @GetMapping("/leak")
    public String leak() {
        return "leak";
    }
}
