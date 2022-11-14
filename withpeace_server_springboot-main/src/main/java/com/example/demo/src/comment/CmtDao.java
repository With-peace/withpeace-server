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

    /** 댓글 작성자 확인 **/
    public Long checkCmtUser(Integer commentIdx){
        String checkCmtUserQuery = "select userIdx from Comment where commentIdx = ?";
        Integer checkCmtUserParams = commentIdx;
        return this.jdbcTemplate.queryForObject(checkCmtUserQuery, Long.class, checkCmtUserParams);
    }

    /** 댓글 수정 - Comment **/
    public int updateCmt(Integer commentIdx, PatchCommentsReq patchCommentsReq){
        String updateCmtQuery = "UPDATE Comment SET content=? WHERE commentIdx=?";
        Object[] updateCmtParams = new Object[] {patchCommentsReq.getContent(), commentIdx};
        return this.jdbcTemplate.update(updateCmtQuery, updateCmtParams);
    }
}
