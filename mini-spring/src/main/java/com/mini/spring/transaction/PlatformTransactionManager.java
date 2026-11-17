package com.mini.spring.transaction;

/**
 * 事务管理器接口。存在的原因是避免jdbc与transaction包互相依赖形成环。
 * 保证依赖方向只有jdbc->transaction
 */
public interface PlatformTransactionManager {
    void begin();

    void commit();

    void rollback();

    void cleanup();
}
