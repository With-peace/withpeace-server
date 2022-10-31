package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.post.*;
import com.example.demo.src.auth.*;
import com.example.demo.src.post.model.*;
import com.example.demo.src.user.UserDao;
import com.example.demo.src.user.UserProvider;
import com.example.demo.src.user.model.PostUserManagerReq;
import com.example.demo.src.user.model.PostUserManagerRes;
import com.example.demo.utils.*;
import com.example.demo.utils.SHA256;
import org.apache.commons.lang3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.lang.Long;
import java.math.BigInteger;


import javax.sql.DataSource;
import java.lang.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class PostService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PostDao postDao;
    private final AuthDao authDao;
    private final PostProvider postProvider;
    private final JwtService jwtService;
    PlatformTransactionManager transactionManager;
    private DataSource dataSource;


    @Autowired
    public PostService(PostDao postDao, AuthDao authDao, PostProvider postProvider, JwtService jwtService) {
        this.postDao = postDao;
        this.authDao = authDao;
        this.postProvider = postProvider;
        this.jwtService = jwtService;

    }

    /** 게시글 생성 **/
    @Transactional
    public PostPostsRes createPost(List<String> postImage, PostPostsReq postPostsReq, String accessToken) throws BaseException {

        try{
            // 사용자의 userLevle 체크
            String userLevel = postProvider.getUserLevel(postPostsReq.getUserIdx());

            // Post - Post
            // userIdx, title, content, isAnonymous
            int postIdx = postDao.insertPost(postPostsReq);
            System.out.println("추가된 postIdx : "+postIdx);

            // Post - PostImage
            // postIdx, postImage
            if(postImage != null){
                for(int i=0; i<postImage.size(); i++){
                    int postImageUrl = postDao.insertPostImage(postIdx, postImage.get(i));
                    System.out.println("추가된 postImageUrl : "+postImageUrl);
                }
            }

            // 추가된 게시글인덱스
            return new PostPostsRes(postPostsReq.getUserIdx(), userLevel, postIdx, accessToken);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /** 게시글 삭제 **/
    @Transactional
    public PostDeleteRes deletePost(Integer postIdx, Long userIdx, String accessToken) throws BaseException {

        // 게시글 존재여부 확인
        if(postProvider.checkPost(postIdx) == 0){
            throw new BaseException(POST_DELETE_INVALID_POSTIDX);
        }

        // 유저가 접근가능한 게시글인지 확인
        if(postProvider.checkPostUser(postIdx, userIdx) == false){
            throw new BaseException(POST_DELETE_INVALID_USER);
        }

        try{
            // Post - PATCH
            // postIdx
            postDao.deletePost(postIdx);
            System.out.println("삭제된 postIdx : "+postIdx);

            // PostImage - GET
            // postIdx
            List<String> postImageUrls = postDao.getPostImage(postIdx);

            // 파일 삭제
            boolean deleteResult = deletePostImage(postImageUrls);
            if(!deleteResult){
                // 파일 삭제에 실패한 경우
                throw new BaseException(POST_DELETE_POSTIMAGE);
            }

            // PostImage - PATCH
            // postIdx
            postDao.deletePostImage(postIdx);

            // 삭제된 게시글 인덱스
            return new PostDeleteRes(postIdx, accessToken);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /** 게시글 삭제 - 이미지 파일 삭제 **/
    @Transactional
    public boolean deletePostImage(List<String> postImageUrls) {

        if (postImageUrls.size() == 0) {
            // 게시글에 이미지가 없는 경우
            return true;
        }

        try{
            boolean result = false;

            for(int i=0; i<postImageUrls.size(); i++){
                File file = new File(postImageUrls.get(i));
                result = file.delete();
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    /** 게시글 좋아요 **/
    @Transactional
    public PostLikeRes createPostLike(Long userIdx, Integer postIdx, String accessToken) throws BaseException {

        // 게시글 존재여부 확인
        if(postProvider.checkPost(postIdx) == 0){
            throw new BaseException(POST_DELETE_INVALID_POSTIDX);
        }

        // 사용자의 userLevle 체크
        String userLevel = postProvider.getUserLevel(userIdx);

        try{
            // Post - PostLike
            // userIdx, postIdx
            int postLikeIdx = postDao.insertPostLike(userIdx, postIdx);
            System.out.println("추가된 postLikeIdx : "+postLikeIdx);

            // 추가된 게시글좋아요 인덱스
            return new PostLikeRes(userIdx, userLevel, postLikeIdx, accessToken);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /** 게시글 좋아요 취소 **/
    @Transactional
    public PostLikeRes deletePostLike(Long userIdx, Integer postLikeIdx, String accessToken) throws BaseException {

        // 게시글 좋아요 존재여부 확인
        if(postProvider.checkPostLike(postLikeIdx) == 0){
            throw new BaseException(POST_DELETE_INVALID_POSTLIKEIDX);
        }

        // 유저가 접근가능한 게시글 좋아요 인지 확인
        if(postProvider.checkPostLikeUser(postLikeIdx, userIdx) == false){
            throw new BaseException(POST_DELETE_INVALID_USER);
        }

        // 사용자의 userLevle 체크
        String userLevel = postProvider.getUserLevel(userIdx);

        try{
            // Delete - PostLike
            // postLikeIdx, userIdx
            postDao.deletePostLike(postLikeIdx);
            System.out.println("삭제된 postLikeIdx : "+postLikeIdx);

            // 삭제된 게시글좋아요 인덱스
            return new PostLikeRes(userIdx,userLevel, postLikeIdx, accessToken);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /** 게시글 저장 **/
    @Transactional
    public PostSaveRes createPostSave(Long userIdx, Integer postIdx, String accessToken) throws BaseException {

        // 게시글 존재여부 확인
        if(postProvider.checkPost(postIdx) == 0){
            throw new BaseException(POST_DELETE_INVALID_POSTIDX);
        }

        // 사용자의 userLevle 체크
        String userLevel = postProvider.getUserLevel(userIdx);

        try{
            // Post - PostSave
            // userIdx, postIdx
            int postSaveIdx = postDao.insertPostSave(userIdx, postIdx);
            System.out.println("추가된 postSaveIdx : "+postSaveIdx);

            // 추가된 게시글저장 인덱스
            return new PostSaveRes(userIdx, userLevel, postSaveIdx, accessToken);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /** 게시글 저장 취소 **/
    @Transactional
    public PostSaveRes deletePostSave(Long userIdx, Integer postSaveIdx, String accessToken) throws BaseException {

        // 게시글 저장 존재여부 확인
        if(postProvider.checkPostSave(postSaveIdx) == 0){
            throw new BaseException(POST_DELETE_INVALID_POSTSAVEIDX);
        }

        // 유저가 접근가능한 게시글 좋아요 인지 확인
        if(postProvider.checkPostSaveUser(postSaveIdx, userIdx) == false){
            throw new BaseException(POST_DELETE_INVALID_USER);
        }

        // 사용자의 userLevle 체크
        String userLevel = postProvider.getUserLevel(userIdx);

        try{
            // Delete - PostSave
            // postSaveIdx
            postDao.deletePostSave(postSaveIdx);
            System.out.println("삭제된 postSaveIdx : "+postSaveIdx);

            // 삭제된 게시글저장 인덱스
            return new PostSaveRes(userIdx, userLevel, postSaveIdx, accessToken);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
