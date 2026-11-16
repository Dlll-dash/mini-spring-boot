package com.mini.spring.dao;

public class IncorrectResultSizeDataAccessException extends DataAccessException {
    public IncorrectResultSizeDataAccessException(String message) {
        super(message);
    }
}
