package com.mini.spring.fixture.aop;

import com.mini.spring.annotation.Bean;
import com.mini.spring.annotation.ComponentScan;
import com.mini.spring.transaction.EnableTransactionManagement;
import com.mini.spring.transaction.PlatformTransactionManager;

@EnableTransactionManagement
@ComponentScan
public class AopApp {

    /** 事务管理器在 invoke 时才被取，这里换成不碰数据库的录制版 */
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new RecordingTxManager();
    }
}
