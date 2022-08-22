package com.example.demo.src.post;

import com.example.demo.src.post.model.PostPostsReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.lang.Long;
import java.util.List;

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

    /** 게시글 존재여부 확인 **/
    public int checkPost(Integer postIdx){
        String checkEmailQuery = "select exists(select postIdx from Post where postIdx = ? and status='ACTIVE')";
        Integer checkEmailParams = postIdx;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);
    }

    /** 유저가 접근가능한 게시글인지 확인 **/
    public Long checkPostUser(Integer postIdx){
        String checkPostUserQuery = "select userIdx from Post where postIdx = ? and status='ACTIVE'";
        Integer checkPostUserParams = postIdx;
        return this.jdbcTemplate.queryForObject(checkPostUserQuery, // 리스트면 query, 리스트가 아니면 queryForObject
                (rs,rowNum) -> rs.getLong("userIdx"), checkPostUserParams);
    }

    /** 게시글 삭제 - Post **/
    public int deletePost(Integer postIdx){
        String deletePostQuery = "UPDATE Post SET status='DELETED' WHERE postIdx=?";
        Object[] deletePostParams = new Object[] {postIdx};
        return this.jdbcTemplate.update(deletePostQuery, deletePostParams);

    }

    /** 게시글 삭제 - 이미지 파일의 경로 get - PostImage **/
    public List<String> getPostImage(Integer postIdx){
        // Get - PostImage
        // postImageUrl
        String getPostImageQuery = "select postImageUrl from PostImage where postIdx=? and status='ACTIVE'";
        Integer getPostImageParams = postIdx;

        List<String> postImageUrls = this.jdbcTemplate.query(getPostImageQuery, // 리스트면 query, 리스트가 아니면 queryForObject
                (rs,rowNum) -> rs.getString("postImageUrl"), getPostImageParams);

        return postImageUrls;
    }

    /** 게시글 삭제 - PostImage **/
    public int deletePostImage(Integer postIdx){
        String deletePostQuery = "UPDATE PostImage SET status='DELETED' WHERE postIdx=?";
        Object[] deletePostParams = new Object[] {postIdx};
        return this.jdbcTemplate.update(deletePostQuery, deletePostParams);

    }

    /** 게시글 좋아요 - PostLike **/
    public int insertPostLike(Long userIdx, Integer postIdx){
        // Post - PostLike
        // userIdx, postIdx
        String insertPostLikeQuery = "insert into PostLike (userIdx, postIdx) VALUES (?,?)";

        Object[] insertPostLikeParams = new Object[]{
                userIdx,
                postIdx};

        this.jdbcTemplate.update(insertPostLikeQuery, insertPostLikeParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    /** 게시글 좋아요 존재여부 확인 **/
    public int checkPostLike(Integer postLikeIdx){
        String checkPostLikeQuery = "select exists(select postLikeIdx from PostLike where postLikeIdx = ?)";
        Integer checkPostLikeParams = postLikeIdx;
        return this.jdbcTemplate.queryForObject(checkPostLikeQuery,
                int.class,
                checkPostLikeParams);
    }

    /** 유저가 접근가능한 게시글 좋아요인지 확인 **/
    public Long checkPostLikeUser(Integer postLikeIdx){
        String checkPostLikeUserQuery = "select userIdx from PostLike where postLikeIdx = ?";
        Integer checkPostLikeUserParams = postLikeIdx;
        return this.jdbcTemplate.queryForObject(checkPostLikeUserQuery, // 리스트면 query, 리스트가 아니면 queryForObject
                (rs,rowNum) -> rs.getLong("userIdx"), checkPostLikeUserParams);
    }

    /** 게시글 좋아요 취소 - PostLike **/
    public void deletePostLike(Integer postLikeIdx){
        // Delete - PostLike
        // postLikeIdx
        String deletePostLikeQuery = "delete from PostLike where postLikeIdx = ?";

        Integer deletePostLikeParams = postLikeIdx;

        this.jdbcTemplate.update(deletePostLikeQuery, deletePostLikeParams);
    }

}
