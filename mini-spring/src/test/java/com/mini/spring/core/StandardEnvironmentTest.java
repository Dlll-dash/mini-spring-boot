package com.mini.spring.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StandardEnvironmentTest {

    private final StandardEnvironment environment = new StandardEnvironment();

    @Test
    void readsFromSystemProperties() {
        assertEquals(System.getProperty("java.version"), environment.getProperty("java.version"));
    }

    @Test
    void textWithoutPlaceholderIsReturnedAsIs() {
        assertEquals("hello", environment.resolvePlaceholders("hello"));
    }

    @Test
    void resolvesKnownKey() {
        assertEquals(System.getProperty("java.version"),
                environment.resolvePlaceholders("${java.version}"));
    }

    @Test
    void resolvesPlaceholderInsideSurroundingText() {
        assertEquals("a-" + System.getProperty("java.version") + "-b",
                environment.resolvePlaceholders("a-${java.version}-b"));
    }

    @Test
    void usesDefaultValueWhenKeyIsMissing() {
        assertEquals("fallback", environment.resolvePlaceholders("${mini.test.missing:fallback}"));
    }

    @Test
    void defaultCanBeTheEmptyString() {
        assertEquals("", environment.resolvePlaceholders("${mini.test.missing:}"));
    }

    @Test
    void keepsPlaceholderWhenKeyAndDefaultAreBothMissing() {
        assertEquals("${mini.test.missing}", environment.resolvePlaceholders("${mini.test.missing}"));
    }

    @Test
    void unclosedPlaceholderIsLeftAlone() {
        assertEquals("${oops", environment.resolvePlaceholders("${oops"));
    }
}
