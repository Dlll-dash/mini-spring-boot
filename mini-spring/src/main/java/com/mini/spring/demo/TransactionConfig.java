package com.mini.spring.demo;

import com.mini.spring.transaction.EnableTransactionManagement;

/**
 * 演示用的普通配置类：经 Main 上的 @Import 进来，验证 @Import 的第三个分支。
 * 正常用法是把 @EnableTransactionManagement 直接写在配置类上，不必多这一层。
 */
@EnableTransactionManagement(order = -1)
public class TransactionConfig {
}
