package com.mini.spring;

import com.mini.spring.fixture.bean.BeanApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BeanMethodTest {

    private MiniContext context;

    @BeforeEach
    void refreshContainer() {
        context = new MiniContext(BeanApp.class);
        context.refresh();
    }

    @Test
    void beanMethodIsRegisteredUnderTheMethodName() {
        List<String> names = List.of(context.getBeanDefinitionNames());

        assertTrue(names.contains("builder"), names.toString());
        assertTrue(names.contains("greeting"), names.toString());
    }

    @Test
    void beanMethodReturnValueIsTheBean() {
        assertEquals("made by @Bean", context.getBean("builder").toString());
    }

    @Test
    void beanMethodParameterIsResolvedFromTheContainer() {
        assertEquals("hello from made by @Bean", context.getBean("greeting"));
    }
}
