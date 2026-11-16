package com.mini.spring.dao;

public class EmptyResultDataAccessException extends IncorrectResultSizeDataAccessException{
    public EmptyResultDataAccessException(String message) {
        super(message);
    }
}
