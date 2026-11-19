package com.mini.spring;

import com.mini.spring.exception.NoUniqueBeanDefinitionException;
import com.mini.spring.fixture.ambiguous.AmbiguousApp;
import com.mini.spring.fixture.ambiguous.Speaker;
import com.mini.spring.fixture.primary.AlphaGreeter;
import com.mini.spring.fixture.primary.BetaGreeter;
import com.mini.spring.fixture.primary.Greeter;
import com.mini.spring.fixture.primary.PrimaryApp;
import com.mini.spring.fixture.primary.Watcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrimaryTest {

    MiniContext context;

    @BeforeEach
    void refreshContainer(){
        context = new MiniContext(PrimaryApp.class);
        context.refresh();
    }

    // ---- 类上标 @Primary ----

    @Test
    void returnThePrimaryBean(){
        assertSame(context.getBean(BetaGreeter.class),context.getBean(Greeter.class));
    }

    @Test
    void primaryDoesNotFilterOneToManyLookup(){
        List<Greeter> all = context.getFactory().getBeanOfType(Greeter.class);

        assertEquals(2, all.size());
        assertTrue(all.stream().anyMatch(g -> g instanceof AlphaGreeter));
    }

    // ---- @Bean 方法上标 @Primary ----

    @Test
    void returnThePrimaryMethodBean(){
        assertSame(context.getBean(Watcher.class),context.getBean("mainWatcher"));
    }

    // ---- 谁也不标：必须抛，并且点名两个候选 ----

    @Test
    void throwsWhenTwoCandidatesAndNoPrimary(){
        MiniContext ambiguous = new MiniContext(AmbiguousApp.class);
        ambiguous.refresh();

        NoUniqueBeanDefinitionException e = assertThrows(NoUniqueBeanDefinitionException.class,
                () -> ambiguous.getBean(Speaker.class));

        assertTrue(e.getMessage().contains("firstSpeaker"), e.getMessage());
        assertTrue(e.getMessage().contains("secondSpeaker"), e.getMessage());
    }
}
