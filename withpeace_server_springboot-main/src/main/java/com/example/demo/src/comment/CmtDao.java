package com.example.demo.src.comment;


import com.example.demo.src.comment.model.*;
import com.example.demo.src.post.model.PostPostsReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class CmtDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /** 댓글 생성 - Comment **/
    public int insertCmt(Integer postIdx, PostCommentsReq postCommentsReq){
        // Post - Comment
        // userIdx, postIdx, content, isAnonymous
        String insertCmtQuery = "insert into Comment (userIdx, postIdx, content, isAnonymous) VALUES (?,?,?,?)";

        Object[] insertCmtParams = new Object[]{
                postCommentsReq.getUserIdx(),
                postIdx,
                postCommentsReq.getContent(),
                postCommentsReq.getIsAnonymous()};

        this.jdbcTemplate.update(insertCmtQuery, insertCmtParams);
        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }
}
