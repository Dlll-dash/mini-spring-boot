package com.mini.spring.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleTypeConverterTest {

    @Test
    void convertsToPrimitiveInt() {
        assertEquals(42, SimpleTypeConverter.convertIfNecessary("42", int.class));
    }

    @Test
    void convertsToBoxedInteger() {
        assertEquals(42, SimpleTypeConverter.convertIfNecessary("42", Integer.class));
    }

    @Test
    void emptyStringBecomesNullForBoxedType() {
        assertNull(SimpleTypeConverter.convertIfNecessary("", Integer.class));
    }

    @Test
    void emptyStringCannotBeNullForPrimitive() {
        assertThrows(NumberFormatException.class,
                () -> SimpleTypeConverter.convertIfNecessary("", int.class));
    }

    @Test
    void unsupportedTypeFailsLoudly() {
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> SimpleTypeConverter.convertIfNecessary("1", long.class));

        assertTrue(e.getMessage().contains("long"), e.getMessage());
    }
}
