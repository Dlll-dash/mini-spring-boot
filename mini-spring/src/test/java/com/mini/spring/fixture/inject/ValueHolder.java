package com.mini.spring.fixture.inject;

import com.mini.spring.annotation.Component;
import com.mini.spring.annotation.Value;

@Component
public class ValueHolder {

    @Value("${mini.test.name:default-name}")
    private String name;

    @Value("${mini.test.hits:7}")
    private int hits;

    @Value("${mini.test.enabled:true}")
    private boolean enabled;

    /** 系统属性里没有，也没有默认值：应当原样保留 ${...} */
    @Value("${mini.test.missing}")
    private String missing;

    public String getName() {
        return name;
    }

    public int getHits() {
        return hits;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getMissing() {
        return missing;
    }
}
