package com.mini.spring.core;

public interface Environment {

    String getProperty(String key);

    String resolvePlaceholders(String text);

}
