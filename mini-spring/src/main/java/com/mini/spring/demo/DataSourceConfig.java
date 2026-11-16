package com.mini.spring.demo;

import com.mini.spring.annotation.Bean;
import com.mini.spring.annotation.Configuration;
import com.mini.spring.annotation.Value;
import com.mini.spring.jdbc.core.JdbcTemplate;
import com.mini.spring.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Value("${mini.db.url}")
    private String url;

    @Value("${mini.db.user}")
    private String user;

    @Value("${mini.db.password}")
    private String password;

    private DataSource dataSource;

    @Bean
    public DataSource dataSource(){
        dataSource = new DriverManagerDataSource(url,user,password);
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource){ return new JdbcTemplate(dataSource);}
}
