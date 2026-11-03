package com.mini.spring.annotation;

public enum FilterType {
    /**
     * 表示过滤的是注释，判断注释类型是否符合过滤条件
     */
    ANNOTATION,

    /**
     * 表示过滤的是可赋值类型，比如判断接口实现和父类继承等关系
     */
    ASSIGNABLE_TYPE,

    /**
     * 表示就是过滤器，直接实例化（并调用Aware）
     */
    CUSTOM
}
