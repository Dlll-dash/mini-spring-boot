package com.mini.spring.transaction;

import com.mini.spring.beans.MiniBeanFactory;
import com.mini.spring.aop.MethodInterceptor;
import com.mini.spring.aop.MethodInvocation;

public class TransactionInterceptor implements MethodInterceptor {

    private MiniBeanFactory factory;

    TransactionInterceptor(MiniBeanFactory factory){
        this.factory = factory;
    }

    /**
     * 使用事务包住拦截器方法，失败时回滚数据库操作。
     * @param invocation
     * @return
     */
    @Override
    public Object invoke(MethodInvocation invocation){
        PlatformTransactionManager tm = factory.getBean(PlatformTransactionManager.class);
        tm.begin();
        System.out.println("[tx] begin");
        Object result;
        try {
            result = invocation.proceed();
            tm.commit();
            System.out.println("[tx] commit");
            return result;
        }catch(Throwable e){
            System.out.println("[tx] rollback");
            tm.rollback();
            throw e;
        }finally {
            tm.cleanup();
        }


    }

}
