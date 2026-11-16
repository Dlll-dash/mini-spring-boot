package com.mini.spring.demo;

import com.mini.spring.annotation.Autowired;
import com.mini.spring.annotation.Component;
import com.mini.spring.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Component
public class UserQueryService {
    @Autowired
    DataSource dataSource;
    @Autowired
    JdbcTemplate jdbcTemplate;

    public String findName(long id){
            return jdbcTemplate.queryForObject("SELECT name from t_user where id = ?",(rs, rowNum) -> rs.getString(1) ,id);
    }
}
