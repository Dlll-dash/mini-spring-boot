package com.mini.spring.jdbc.core;

import com.mini.spring.dao.DataAccessException;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PreparedStatementCallback<T> {

    T doInPreparedStatement(PreparedStatement ps)throws SQLException, DataAccessException;

}
