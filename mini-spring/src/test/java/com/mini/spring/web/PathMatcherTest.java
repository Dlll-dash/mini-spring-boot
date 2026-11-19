package com.mini.spring.web;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PathMatcherTest {

    private final PathMatcher matcher = new PathMatcher();

    @Test
    void identicalLiteralPathMatches() {
        Map<String, String> vars = matcher.match("/hello", "/hello");

        assertNotNull(vars);
        assertTrue(vars.isEmpty());
    }

    @Test
    void differentSegmentCountDoesNotMatch() {
        assertNull(matcher.match("/hello", "/hello/x"));
        assertNull(matcher.match("/hello/x", "/hello"));
    }

    @Test
    void literalSegmentIsCompared() {
        assertNull(matcher.match("/hello", "/hallo"));
    }

    @Test
    void variableSegmentIsCaptured() {
        Map<String, String> vars = matcher.match("/item/{id}", "/item/42");

        assertNotNull(vars);
        assertEquals("42", vars.get("id"));
    }

    @Test
    void variableSegmentMatchesOnlyOneSegment() {
        assertNull(matcher.match("/item/{id}", "/item/1/2"));
    }

    @Test
    void severalVariablesAreCapturedTogether() {
        Map<String, String> vars = matcher.match("/{a}/{b}", "/x/y");

        assertNotNull(vars);
        assertEquals("x", vars.get("a"));
        assertEquals("y", vars.get("b"));
    }

    @Test
    void literalSegmentsCountOnlyFixedSegments() {
        assertEquals(1, matcher.literalSegments("/a/{b}"));
        assertEquals(0, matcher.literalSegments("/{a}/{b}"));
        assertEquals(3, matcher.literalSegments("/a/b/c"));
    }
}
