package com.mini.spring.fixture.core;

import com.mini.spring.annotation.ComponentScan;

/**
 * 测试夹具的种子类。
 *
 * 裸 @ComponentScan 走的是"默认配置类所在包"分支，扫描 com.mini.spring.fixture。
 * 不写包名字符串，是为了搬包时不会静默失效。
 */
@ComponentScan
public class TestApp {
}
