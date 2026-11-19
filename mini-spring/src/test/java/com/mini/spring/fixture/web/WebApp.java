package com.mini.spring.fixture.web;

import com.mini.spring.annotation.ComponentScan;
import com.mini.spring.annotation.Import;
import com.mini.spring.web.HandlerMapping;

@ComponentScan
@Import(HandlerMapping.class)
public class WebApp {
}
