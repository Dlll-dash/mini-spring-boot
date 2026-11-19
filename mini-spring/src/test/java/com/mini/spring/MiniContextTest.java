package com.mini.spring;

import com.mini.spring.fixture.core.Alpha;
import com.mini.spring.fixture.core.Beta;
import com.mini.spring.fixture.core.TestApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MiniContextTest {

    private MiniContext context;

    @BeforeEach
    void refreshContainer() {
        context = new MiniContext(TestApp.class);
        context.refresh();
    }

    @Test
    void scansComponentClassesFromTheSeedPackage() {
        List<String> names = List.of(context.getBeanDefinitionNames());

        assertTrue(names.containsAll(List.of("testApp", "alpha", "beta")), names.toString());
    }

    @Test
    void returnsTheSameInstanceEveryTime() {
        assertSame(context.getBean(Alpha.class), context.getBean(Alpha.class));
    }

    @Test
    void injectsAutowiredFields() {
        Alpha alpha = context.getBean(Alpha.class);

        assertNotNull(alpha.getBeta());
    }

    @Test
    void resolvesCircularDependencies() {
        Alpha alpha = context.getBean(Alpha.class);
        Beta beta = context.getBean(Beta.class);

        assertSame(beta, alpha.getBeta());
        assertSame(alpha, beta.getAlpha());
    }
}
