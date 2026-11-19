package com.mini.spring.fixture.qualifier;

import com.mini.spring.annotation.Component;
import com.mini.spring.annotation.Primary;

@Component
@Primary
public class LoudAnnouncer implements Announcer {

    @Override
    public String announce() {
        return "loud";
    }
}
