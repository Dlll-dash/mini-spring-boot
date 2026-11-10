package com.mini.spring.web.exception;

public class MissingPathVariableException extends RuntimeException {
    public MissingPathVariableException(String message) {
        super(message);
    }
}
