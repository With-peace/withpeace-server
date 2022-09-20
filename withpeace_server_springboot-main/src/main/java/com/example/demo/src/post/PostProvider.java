package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.post.model.*;
import com.example.demo.src.post.*;
import com.example.demo.src.user.UserDao;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class PostProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PostDao postDao;
    private final JwtService jwtService;

    @Autowired
    public PostProvider(PostDao postDao, JwtService jwtService) {
        this.postDao = postDao;
        this.jwtService = jwtService;
    }

    /** 게시글 존재여부 확인 **/
    public int checkPost(Integer postIdx) throws BaseException{
        try{
            return postDao.checkPost(postIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 유저가 접근가능한 게시글인지 확인 **/
    public boolean checkPostUser(Integer postIdx, Long userIdx) throws BaseException{
        try{
            Long postUserIdx = postDao.checkPostUser(postIdx);
            if(postUserIdx == userIdx){
                return true;
            }
            else{
                return false;
            }
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 게시글 좋아요 존재여부 확인 **/
    public int checkPostLike(Integer postLikeIdx) throws BaseException{
        try{
            return postDao.checkPostLike(postLikeIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 유저가 접근가능한 게시글 좋아요인지 확인 **/
    public boolean checkPostLikeUser(Integer postLikeIdx, Long userIdx) throws BaseException{
        try{
            Long postLikeUserIdx = postDao.checkPostLikeUser(postLikeIdx);
            if(postLikeUserIdx == userIdx){
                return true;
            }
            else{
                return false;
            }
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 게시글 저장 존재여부 확인 **/
    public int checkPostSave(Integer postSaveIdx) throws BaseException{
        try{
            return postDao.checkPostSave(postSaveIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 유저가 접근가능한 게시글 저장인지 확인 **/
    public boolean checkPostSaveUser(Integer postSaveIdx, Long userIdx) throws BaseException{
        try{
            Long postSaveUserIdx = postDao.checkPostSaveUser(postSaveIdx);
            if(postSaveUserIdx == userIdx){
                return true;
            }
            else{
                return false;
            }
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 게시글 조회 **/
    public GetPostRes getPost(Long userIdx, int postIdx, String accessToken) throws BaseException {

        try{
            GetPostRes getPost = postDao.selectPost(userIdx, postIdx, accessToken);

            return getPost;
        }
        catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 관리자 공지 리스트 조회 **/
    public GetNoticeListRes getNoticeList(Long userIdx, String accessToken) throws BaseException {

        try{
            List<GetNoticeList> getNoticeList = postDao.selectNoticeList(userIdx);

            GetNoticeListRes getNoticeListRes = new GetNoticeListRes(getNoticeList, accessToken);

            return getNoticeListRes;
        }
        catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
