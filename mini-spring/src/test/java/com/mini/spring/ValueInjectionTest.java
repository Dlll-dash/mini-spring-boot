package com.mini.spring;

import com.mini.spring.fixture.inject.InitProbe;
import com.mini.spring.fixture.inject.InjectApp;
import com.mini.spring.fixture.inject.ValueHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValueInjectionTest {

    private ValueHolder holder;

    @BeforeEach
    void refreshContainer() {
        InitProbe.calls = 0;

        MiniContext context = new MiniContext(InjectApp.class);
        context.refresh();
        holder = context.getBean(ValueHolder.class);
    }

    @Test
    void defaultValuesAreUsedWhenKeysAreMissing() {
        assertEquals("default-name", holder.getName());
        assertEquals(7, holder.getHits());
        assertTrue(holder.isEnabled());
    }

    @Test
    void placeholderWithoutValueIsKeptAsWritten() {
        assertEquals("${mini.test.missing}", holder.getMissing());
    }

    @Test
    void postConstructRunsOncePerBean() {
        assertEquals(1, InitProbe.calls);
    }
}
