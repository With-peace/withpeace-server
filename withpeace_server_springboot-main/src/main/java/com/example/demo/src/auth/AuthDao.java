package com.example.demo.src.auth;

import com.example.demo.src.auth.model.PostLoginReq;
import com.example.demo.src.auth.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class AuthDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /** 일반 로그인 - 유저 정보 조회 **/
    public UserInfo getUserInfo(PostLoginReq postLoginReq){
        // Get - User
        // userIdx, password
        String getUserInfoQuery = "select userIdx, password from User where email=? and status='ACTIVE'";
        String getUserInfoParamstParams = postLoginReq.getEmail();

        return this.jdbcTemplate.queryForObject(getUserInfoQuery, // 리스트면 query, 리스트가 아니면 queryForObject
                (rs,rowNum) -> new UserInfo(
                        rs.getInt("userIdx"),
                        rs.getString("password")
                ), getUserInfoParamstParams);
    }
}
