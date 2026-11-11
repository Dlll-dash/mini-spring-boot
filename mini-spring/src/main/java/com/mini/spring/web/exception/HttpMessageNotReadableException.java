package com.mini.spring.web.exception;

public class HttpMessageNotReadableException extends RuntimeException {
    public HttpMessageNotReadableException(String message) {
        super(message);
    }

    public HttpMessageNotReadableException(String message,Exception e){
        super(message,e);
    }
}
