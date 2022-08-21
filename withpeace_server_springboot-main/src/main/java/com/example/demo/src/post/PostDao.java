package com.example.demo.src.post;

import com.example.demo.src.post.model.PostPostsReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.lang.Long;

import javax.sql.DataSource;

@Repository
public class PostDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /** 게시글 생성 - Post **/
    public int insertPost(PostPostsReq postPostsReq){
        // Post - Post
        // userIdx, title, content, isAnonymous
        String insertPostQuery = "insert into Post (userIdx, title, content, type, isAnonymous) VALUES (?,?,?,?,?)";

        Object[] insertPostParams = new Object[]{
                postPostsReq.getUserIdx(),
                postPostsReq.getTitle(),
                postPostsReq.getContent(),
                postPostsReq.getType(),
                postPostsReq.getIsAnonymous()};

        this.jdbcTemplate.update(insertPostQuery, insertPostParams);
        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    /** 게시글 생성 - PostImage **/
    public void insertPostImage(int postIdx, String postImageUrl){
        // Post - PostImage
        // postIdx, postImageUrl
        String insertPostImageQuery = "insert into PostImage (postIdx, postImageUrl) VALUES (?,?)";
        Object[] insertPostImageParams = new Object[]{postIdx, postImageUrl};

        this.jdbcTemplate.update(insertPostImageQuery, insertPostImageParams);
        String lastInsertIdQuery = "select last_insert_id()";
        System.out.println(this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class));

    }

    
}
