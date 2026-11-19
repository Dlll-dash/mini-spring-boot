package com.mini.spring;

import com.mini.spring.fixture.aop.AopApp;
import com.mini.spring.fixture.aop.Checkout;
import com.mini.spring.fixture.aop.OrderService;
import com.mini.spring.fixture.aop.OrderServiceImpl;
import com.mini.spring.fixture.aop.RecordingTxManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProxyTransactionTest {

    private MiniContext context;

    @BeforeEach
    void refreshContainer() {
        RecordingTxManager.calls.clear();

        context = new MiniContext(AopApp.class);
        context.refresh();
    }

    @Test
    void transactionalBeanIsBehindAJdkProxy() {
        Object bean = context.getBean(OrderService.class);

        assertTrue(Proxy.isProxyClass(bean.getClass()));
        assertNotEquals(OrderServiceImpl.class, bean.getClass());
    }

    @Test
    void commitPathCallsBeginThenCommit() {
        assertEquals("placed", context.getBean(OrderService.class).place());

        assertEquals(List.of("begin", "commit", "cleanup"), RecordingTxManager.calls);
    }

    @Test
    void failurePathCallsRollbackAndRethrows() {
        assertThrows(IllegalStateException.class,
                () -> context.getBean(OrderService.class).fail());

        assertEquals(List.of("begin", "rollback", "cleanup"), RecordingTxManager.calls);
    }

    @Test
    void methodWithoutTransactionalSkipsTheChain() {
        assertEquals("plain", context.getBean(OrderService.class).plain());

        assertTrue(RecordingTxManager.calls.isEmpty(), RecordingTxManager.calls.toString());
    }

    @Test
    void injectedFieldHoldsTheSameProxyAsGetBean() {
        OrderService orderService = context.getBean(OrderService.class);
        Checkout checkout = context.getBean(Checkout.class);

        assertNotNull(orderService.getCheckout());
        assertNotNull(checkout.getOrderService());

        assertSame(checkout, orderService.getCheckout());
        assertSame(orderService, checkout.getOrderService());
    }
}
