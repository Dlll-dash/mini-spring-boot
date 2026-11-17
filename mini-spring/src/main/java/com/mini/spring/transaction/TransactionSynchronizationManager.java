package com.mini.spring.transaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class TransactionSynchronizationManager {
    //将事务连接与线程绑定
    private static final ThreadLocal<Map<DataSource, Connection>> resources =
            new ThreadLocal<>();

    /**
     * 获取线程中对应数据库的连接
     * @param key
     * @return 一个数据库的连接
     */
    public static Connection getResource(DataSource key){
        Map<DataSource, Connection> map = resources.get();

        if(map==null){
            return null;
        }

        return map.get(key);
    }

    /**
     * 将数据库的连接绑定到线程上，如果已经存在就抛出异常
     * @param key
     * @param connection
     */
    public static void bindResource(DataSource key,Connection connection){
        Map<DataSource, Connection> map = resources.get();

        if(map==null){
            map = new HashMap<>();
            resources.set(map);
        }
        Connection oldValue = map.put(key, connection);
        if(oldValue!=null){
            //如果已有绑定，则报错
            throw new IllegalStateException("Already value [" + oldValue + "] for key [" + key + "] bound to thread");
        }
    }

    /**
     * 解除对应数据库连接与线程的绑定，如果Map空了，就移除。
     * @param key
     * @return
     */
    public static Connection unbind(DataSource key){
        Map<DataSource, Connection> map = resources.get();

        if(map==null){
            return null;
        }

        Connection connection = map.remove(key);

        if(map.isEmpty()){
            //如果连接空了，就移除整个Map，避免残留跟着线程一直存在
            resources.remove();
        }

        return connection;
    }

}
