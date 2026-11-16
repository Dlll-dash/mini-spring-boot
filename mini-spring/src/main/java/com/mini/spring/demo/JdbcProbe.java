package com.mini.spring.demo;

import com.mini.spring.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcProbe {
    public static void main(String[] args) throws SQLException {
        String url = args[0];
        String user = args[1];
        String password = args[2];
        DataSource ds = new DriverManagerDataSource(url,user,password);
        Connection connection = ds.getConnection();
        try(connection){
            System.out.println(connection.getMetaData());
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT  1");
            System.out.println(preparedStatement.executeQuery());
            System.out.println(connection.isClosed());
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        System.out.println(connection.isClosed());

    }
}
