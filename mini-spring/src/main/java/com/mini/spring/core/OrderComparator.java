package com.mini.spring.core;

import java.util.Comparator;

public class OrderComparator implements Comparator<Object> {
    @Override
    public int compare(Object o1, Object o2) {
        int i1=getOrder(o1);
        int i2=getOrder(o2);

        return Integer.compare(i1,i2);
    }



    protected int getOrder(Object obj){
        if(obj instanceof Ordered){
            return ((Ordered) obj).getOrder();
        }
        return Ordered.LOWEST_PRECEDENCE;
    }

}
