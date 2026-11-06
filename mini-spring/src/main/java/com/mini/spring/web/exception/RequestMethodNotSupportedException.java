package com.mini.spring.web.exception;

public class RequestMethodNotSupportedException extends RuntimeException {

    private final String httpMethod;

    private final String requestURL;

    public RequestMethodNotSupportedException(String httpMethod, String requestURL) {
        super("No endpoint " + httpMethod + " " + requestURL + ".");
        this.httpMethod = httpMethod;
        this.requestURL = requestURL;
    }

    public int getStatusCode(){
        return 405;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getRequestURL() {
        return requestURL;
    }
}
