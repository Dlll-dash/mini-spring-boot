package com.mini.spring.web;

import com.mini.spring.MiniContext;
import com.mini.spring.fixture.web.WebApp;
import com.mini.spring.web.exception.RequestMethodNotSupportedException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HandlerMappingTest {

    private HandlerMapping mapping;

    @BeforeEach
    void refreshContainer() {
        MiniContext context = new MiniContext(WebApp.class);
        context.refresh();
        mapping = context.getBean(HandlerMapping.class);
    }

    @Test
    void mapsPathAndMethodToControllerMethod() {
        Handler handler = mapping.getHandler(request("GET", "/api/hello"));

        assertNotNull(handler);
        assertEquals("probeController", handler.beanName());
        assertEquals("hello", handler.method().getName());
    }

    @Test
    void classLevelPrefixIsPartOfThePath() {
        assertNull(mapping.getHandler(request("GET", "/hello")));
        assertNotNull(mapping.getHandler(request("GET", "/api/hello")));
    }

    @Test
    void unknownPathReturnsNull() {
        assertNull(mapping.getHandler(request("GET", "/api/nope")));
    }

    @Test
    void knownPathWithWrongMethodThrows() {
        assertThrows(RequestMethodNotSupportedException.class,
                () -> mapping.getHandler(request("POST", "/api/hello")));
    }

    @Test
    void pathVariableIsStoredOnTheRequest() {
        HttpServletRequest req = request("GET", "/api/item/42");

        assertNotNull(mapping.getHandler(req));

        Map<?, ?> vars = (Map<?, ?>) req.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        assertEquals("42", vars.get("id"));
    }

    @Test
    void componentWithoutControllerAnnotationIsNotMapped() {
        assertNull(mapping.getHandler(request("GET", "/leak")));
    }

    private static HttpServletRequest request(String method, String uri) {
        Map<String, Object> attributes = new HashMap<>();

        return (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class<?>[]{HttpServletRequest.class},
                (proxy, called, args) -> switch (called.getName()) {
                    case "getMethod" -> method;
                    case "getRequestURI" -> uri;
                    case "setAttribute" -> {
                        attributes.put((String) args[0], args[1]);
                        yield null;
                    }
                    case "getAttribute" -> attributes.get((String) args[0]);
                    case "toString" -> "FakeRequest[" + method + " " + uri + "]";
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> null;
                });
    }
}
