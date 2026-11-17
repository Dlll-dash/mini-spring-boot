package com.mini.spring.jdbc.datasource;

import com.mini.spring.dao.DataAccessException;
import com.mini.spring.transaction.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceUtils {

    /**
     * 获取连接方法。如果事务中有连接，则返回事务的连接，否则新建连接。
     * @param ds
     * @return
     */
    public static Connection getConnection(DataSource ds){
        Connection resource = TransactionSynchronizationManager.getResource(ds);
        if(resource != null){
            return resource;
        }
        try{
            return ds.getConnection();
        } catch (SQLException e) {
            throw new DataAccessException("[jdbc]: Failed to get connection...",e);
        }
    }

    /**
     * 释放连接方法。当这个连接是事务管理器发出的则不关闭，而是等待事务管理器释放后才关闭。
     * 如果没有事务，则直接关闭。
     * @param con
     * @param ds
     */
    public static void releaseConnection(Connection con,DataSource ds){
        if(con == null){
            return;
        }
        if(ds!=null) {
            Connection resource = TransactionSynchronizationManager.getResource(ds);

            if (con == resource) {
                //两者是同一个实例，是事务管理器还在使用的连接：不关，直接返回
                return;
            }
        }
        try {
            //非事务连接，或者事务已经释放的连接，直接关闭
            con.close();
        } catch (SQLException e) {
            System.out.println("[jdbc]: Error occurred when the connection was closing");
        }
    }

}
