package com.mini.spring.core;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;

class OrderComparatorTest {

    @Test
    void smallerOrderComesFirst() {
        Ordered later = () -> 5;
        Ordered earlier = () -> -1;

        List<Object> list = new ArrayList<>(List.of(later, earlier));
        list.sort(new OrderComparator());

        assertSame(earlier, list.get(0));
        assertSame(later, list.get(1));
    }

    @Test
    void nonOrderedGoesLast() {
        Object plain = new Object();
        Ordered first = () -> Ordered.HIGHEST_PRECEDENCE;

        List<Object> list = new ArrayList<>(List.of(plain, first));
        list.sort(new OrderComparator());

        assertSame(first, list.get(0));
        assertSame(plain, list.get(1));
    }
}
