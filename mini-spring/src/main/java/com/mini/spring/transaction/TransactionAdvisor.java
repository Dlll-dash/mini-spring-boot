package com.mini.spring.transaction;

import com.mini.spring.BeanFactoryAware;
import com.mini.spring.MiniBeanFactory;
import com.mini.spring.Ordered;
import com.mini.spring.aop.Advisor;
import com.mini.spring.aop.MethodInterceptor;
import com.mini.spring.aop.Pointcut;

public class TransactionAdvisor implements Advisor, Ordered, BeanFactoryAware {
    private Pointcut pointcut;

    private MethodInterceptor advice;

    private int order;

    private MiniBeanFactory factory;

    TransactionAdvisor(int order){
        this.order=order;
    }

    @Override
    public void setBeanFactory(MiniBeanFactory factory) {
        this.factory = factory;
    }

    @Override
    public Pointcut getPointcut() {
        if(pointcut !=null) {
            return this.pointcut;
        }
        return this.pointcut =new TransactionPointcut();
    }

    @Override
    public MethodInterceptor getAdvice() {
        if(advice!=null) {
            return this.advice;
        }
        return this.advice=new TransactionInterceptor(factory);
    }

    @Override
    public int getOrder() {
        return order;
    }
}
