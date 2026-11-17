package com.mini.spring.jdbc.datasource;

import com.mini.spring.transaction.PlatformTransactionManager;
import com.mini.spring.transaction.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceTransactionManager implements PlatformTransactionManager {

    private DataSource dataSource;

    public DataSourceTransactionManager(DataSource dataSource){
        this.dataSource = dataSource;
    }

    @Override
    public void begin() {
        Connection con = null;

        try{
            //直接获取连接
            con = dataSource.getConnection();
            //关闭自动提交
            con.setAutoCommit(false);
            //绑定事务连接到线程
            TransactionSynchronizationManager.bindResource(dataSource,con);

        } catch (Throwable e) {
            DataSourceUtils.releaseConnection(con,dataSource);
            throw new RuntimeException(e);
        }

    }

    @Override
    public void commit() {
        Connection resource = TransactionSynchronizationManager.getResource(dataSource);
        try {
            resource.commit();
        } catch (SQLException e) {
            throw new RuntimeException("JDBC commit",e);
        }
    }

    @Override
    public void rollback() {
        Connection resource = TransactionSynchronizationManager.getResource(dataSource);
        try {
            resource.rollback();
        } catch (SQLException e) {
            throw new RuntimeException("JDBC rollback",e);
        }
    }

    @Override
    public void cleanup() {
        Connection resource = TransactionSynchronizationManager.getResource(dataSource);
        TransactionSynchronizationManager.unbind(dataSource);
        try {
            resource.setAutoCommit(true);
        } catch (Throwable e) {
            //位于finally块中，不要再抛出，仅做提醒
            System.out.println("Could not reset JDBC Connection after transaction"+e.getMessage());
        }
        DataSourceUtils.releaseConnection(resource,dataSource);
    }
}
