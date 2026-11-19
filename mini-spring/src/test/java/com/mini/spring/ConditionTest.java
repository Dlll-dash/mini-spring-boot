package com.mini.spring;

import com.mini.spring.beans.BeanDefinition;
import com.mini.spring.beans.MiniBeanFactory;
import com.mini.spring.context.annotation.OnMissingBeanCondition;
import com.mini.spring.fixture.conditional.ConditionalApp;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConditionTest {

    @Test
    void beanWithFalseConditionStaysOutOfTheRegistry() {
        MiniContext context = new MiniContext(ConditionalApp.class);
        context.refresh();

        List<String> names = List.of(context.getBeanDefinitionNames());
        assertTrue(names.contains("enabled"), names.toString());
        assertFalse(names.contains("disabled"), names.toString());
    }

    @Test
    void onMissingBeanAnswersFromTheRegistry() {
        MiniBeanFactory registry = new MiniBeanFactory();
        OnMissingBeanCondition condition = new OnMissingBeanCondition();

        assertTrue(condition.matches(ValueHolderType.class, registry));

        registry.registerBeanDefinition("holder",
                new BeanDefinition("holder", ValueHolderType.class));

        assertFalse(condition.matches(ValueHolderType.class, registry));
    }

    private static class ValueHolderType {
    }
}
