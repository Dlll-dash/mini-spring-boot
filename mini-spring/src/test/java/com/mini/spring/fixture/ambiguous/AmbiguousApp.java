package com.mini.spring.fixture.ambiguous;

import com.mini.spring.annotation.ComponentScan;

/**
 * 两个同类型的 bean，都不带 @Primary。
 */
@ComponentScan
public class AmbiguousApp {
}
