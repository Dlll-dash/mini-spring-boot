package com.mini.spring;

import com.mini.spring.fixture.event.EventApp;
import com.mini.spring.fixture.event.PingEvent;
import com.mini.spring.fixture.event.PingListener;
import com.mini.spring.fixture.event.PongListener;
import com.mini.spring.fixture.event.ReadyListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventTest {

    @BeforeEach
    void resetCounters() {
        PingListener.count = 0;
        PongListener.count = 0;
        ReadyListener.count = 0;
    }

    @Test
    void refreshBroadcastsContextRefreshedEvent() {
        MiniContext context = new MiniContext(EventApp.class);
        context.refresh();

        assertEquals(1, ReadyListener.count);
    }

    @Test
    void onlyTheMatchingListenerReceivesTheEvent() {
        MiniContext context = new MiniContext(EventApp.class);
        context.refresh();

        context.publishEvent(new PingEvent(this));

        assertEquals(1, PingListener.count);
        assertEquals(0, PongListener.count);
    }

    @Test
    void publishingAgainReachesTheListenerAgain() {
        MiniContext context = new MiniContext(EventApp.class);
        context.refresh();

        context.publishEvent(new PingEvent(this));
        context.publishEvent(new PingEvent(this));

        assertEquals(2, PingListener.count);
    }
}
