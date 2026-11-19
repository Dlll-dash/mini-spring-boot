package com.mini.spring.fixture.qualifier;

import com.mini.spring.annotation.Autowired;
import com.mini.spring.annotation.Component;
import com.mini.spring.annotation.Qualifier;

@Component
public class Consumer {

    /** 不写 @Qualifier：应当落到 @Primary 的 Loud */
    @Autowired
    Announcer byType;

    /** 写了 @Qualifier：应当压过 @Primary，拿到 Quiet */
    @Autowired
    @Qualifier("quietAnnouncer")
    Announcer byQualifier;

    public Announcer getByType() {
        return byType;
    }

    public Announcer getByQualifier() {
        return byQualifier;
    }
}
