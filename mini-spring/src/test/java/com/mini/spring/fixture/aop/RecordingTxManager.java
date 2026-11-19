package com.mini.spring.fixture.aop;

import com.mini.spring.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;

public class RecordingTxManager implements PlatformTransactionManager {

    public static final List<String> calls = new ArrayList<>();

    @Override
    public void begin() {
        calls.add("begin");
    }

    @Override
    public void commit() {
        calls.add("commit");
    }

    @Override
    public void rollback() {
        calls.add("rollback");
    }

    @Override
    public void cleanup() {
        calls.add("cleanup");
    }
}
