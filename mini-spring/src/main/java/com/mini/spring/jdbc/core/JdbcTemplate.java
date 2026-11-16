package com.mini.spring.jdbc.core;

import com.mini.spring.dao.DataAccessException;
import com.mini.spring.dao.EmptyResultDataAccessException;
import com.mini.spring.dao.IncorrectResultSizeDataAccessException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcTemplate {

    private DataSource dataSource;

    public JdbcTemplate(DataSource dataSource){
        setDataSource(dataSource);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 执行sql语句回调
     * @param sql
     * @param action
     * @return
     * @param <T>
     * @throws DataAccessException
     */
    public <T> T execute(String sql, PreparedStatementCallback<T> action) throws DataAccessException {

        try(//获取连接
            Connection connection = dataSource.getConnection();
            //获取preparedStatement
            PreparedStatement ps = createPreparedStatement(connection, sql)){
            //回调
            return action.doInPreparedStatement(ps);
        }catch (SQLException ex){
            throw new DataAccessException("PreparedStatementCallback for SQL ["+sql+"]",ex);
        }
    }

    /**
     * 执行查询sql语句
     * @param sql
     * @param args
     * @param rse
     * @return
     * @param <T>
     * @throws SQLException
     */
    public <T> T query(String sql,Object[]args,ResultSetExtractor<T> rse){
        return execute(sql, new PreparedStatementCallback<T>() {
            @Override
            public T doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                ResultSet rs = null;

                try {
                    //设置ps的属性
                    if (args != null) {
                        setValues(ps, args);
                    }
                    rs = ps.executeQuery();
                    return rse.extractData(rs);
                }finally {
                    if(rs!=null) {
                        rs.close();
                    }
                }
            }
        });
    }

    /**
     * 面向单个Object的查询，只允许有一个返回值
     * @param sql
     * @param rowMapper
     * @param args
     * @return
     * @param <T>
     * @throws SQLException
     */
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args){
        List<T> results = query(sql, args, new RowMapperResultSetExtractor<T>(rowMapper));
        if(results.isEmpty()){
            throw new EmptyResultDataAccessException("expected 1 row, got 0 for: " + sql);
        }
        if(results.size()>1){
            throw new IncorrectResultSizeDataAccessException("expected 1 row, got " + results.size() + " for: " + sql);
        }
        return results.get(0);
    }

    /**
     * 执行更新语句
     * @param sql
     * @param args
     * @return
     */
    public int update(String sql,Object... args){
        return execute(sql, new PreparedStatementCallback<Integer>() {
            @Override
            public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                setValues(ps,args);
                return ps.executeUpdate();
            }
        });
    }

    /**
     * 创建prepareStatement
     * @param con
     * @param sql
     * @return
     * @throws SQLException
     */
    public PreparedStatement createPreparedStatement(Connection con,String sql) throws SQLException {
        return con.prepareStatement(sql);
    }

    /**
     * 填充属性到 prepareStatement 中
     * @param ps
     * @param args
     * @throws SQLException
     */
    public void setValues(PreparedStatement ps,Object[] args) throws SQLException {
        if(args==null){
            return;
        }
        for(int i=0;i<args.length;i++){
            Object obj = args[i];
            ps.setObject(i+1,obj);
        }
    }
}
