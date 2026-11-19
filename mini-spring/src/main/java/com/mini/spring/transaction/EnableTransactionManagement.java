package com.mini.spring.transaction;

import com.mini.spring.core.Ordered;
import com.mini.spring.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启事务管理
 * 将事务配置类注入到bean容器
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(TransactionSelector.class)
public @interface EnableTransactionManagement {

    /**
     * 事务 advisor 的顺序，值越小越靠外
     */
    int order() default Ordered.LOWEST_PRECEDENCE;
}
