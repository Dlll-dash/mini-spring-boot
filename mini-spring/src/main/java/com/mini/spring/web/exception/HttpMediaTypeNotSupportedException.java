package com.mini.spring.web.exception;

public class HttpMediaTypeNotSupportedException extends RuntimeException {
    public HttpMediaTypeNotSupportedException(String contentType) {
        super("Content-Type '"+contentType+"' is not supported");
    }

    public int getStatusCode(){
        return 415;
    }
}
