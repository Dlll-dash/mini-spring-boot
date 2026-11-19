package com.mini.spring.core;

public interface Ordered {

    /**
     * 最高优先级，Integer的最小值
     */
    int HIGHEST_PRECEDENCE=Integer.MIN_VALUE;

    /**
     * 最低优先级，Integer的最大值
     */
    int LOWEST_PRECEDENCE=Integer.MAX_VALUE;

    /**
     * 值越小优先级越高，越靠前
     */
    public int getOrder();

}
