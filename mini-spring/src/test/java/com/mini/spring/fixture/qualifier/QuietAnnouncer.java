package com.mini.spring.fixture.qualifier;

import com.mini.spring.annotation.Component;

@Component
public class QuietAnnouncer implements Announcer {

    @Override
    public String announce() {
        return "quiet";
    }
}
