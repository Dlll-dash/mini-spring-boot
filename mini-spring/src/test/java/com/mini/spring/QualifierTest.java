package com.mini.spring;

import com.mini.spring.exception.NoSuchBeanDefinitionException;
import com.mini.spring.fixture.qualifier.Announcer;
import com.mini.spring.fixture.qualifier.Consumer;
import com.mini.spring.fixture.qualifier.LoudAnnouncer;
import com.mini.spring.fixture.qualifier.QualifierApp;
import com.mini.spring.fixture.qualifier.QuietAnnouncer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QualifierTest {

    private MiniContext context;

    @BeforeEach
    void refreshContainer() {
        context = new MiniContext(QualifierApp.class);
        context.refresh();
    }

    @Test
    void qualifierPicksTheNamedBean() {
        assertSame(context.getBean(QuietAnnouncer.class),
                context.getFactory().getBean(Announcer.class, "quietAnnouncer"));
    }

    @Test
    void qualifierWinsOverPrimary() {
        Consumer consumer = context.getBean(Consumer.class);

        assertSame(context.getBean(QuietAnnouncer.class), consumer.getByQualifier());
    }

    @Test
    void withoutQualifierThePrimaryIsUsed() {
        Consumer consumer = context.getBean(Consumer.class);

        assertSame(context.getBean(LoudAnnouncer.class), consumer.getByType());
    }

    @Test
    void unknownQualifierThrows() {
        NoSuchBeanDefinitionException e = assertThrows(NoSuchBeanDefinitionException.class,
                () -> context.getFactory().getBean(Announcer.class, "missing"));

        assertTrue(e.getMessage().contains("missing"), e.getMessage());
    }

    @Test
    void qualifierPointingAtAnotherTypeThrows() {
        // qualifierApp 这个名字存在，但类型不是 Announcer
        assertThrows(NoSuchBeanDefinitionException.class,
                () -> context.getFactory().getBean(Announcer.class, "qualifierApp"));
    }
}
