package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.src.post.model.*;
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

    /** 사용자의 userLevel 조회 **/
    public String getUserLevel(Long userIdx) throws BaseException {

        try{
            String getUserLevel = postDao.selectUserLevel(userIdx);

            return getUserLevel;
        }
        catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
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
            // 사용자의 userLevle 체크
            String userLevel = getUserLevel(userIdx);

            GetPostRes getPost = postDao.selectPost(userIdx, userLevel, postIdx, accessToken);

            return getPost;
        }
        catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 관리자 공지 리스트 조회 **/
    public GetPostList getNoticeList(Long userIdx, String accessToken) throws BaseException {

        try{
            // 사용자의 userLevle 체크
            String userLevel = getUserLevel(userIdx);

            List<GetPostInfo> getNoticeList = postDao.selectNoticeList(userIdx);

            GetPostList getNoticeListRes = new GetPostList(userIdx, userLevel, getNoticeList, accessToken);

            return getNoticeListRes;
        }
        catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 자유게시판 리스트 조회 **/
    public GetPostList getGeneralList(Long userIdx, String accessToken) throws BaseException {

        try{
            // 사용자의 userLevle 체크
            String userLevel = getUserLevel(userIdx);

            List<GetPostInfo> getGeneralList = postDao.selectGeneralList(userIdx);

            GetPostList getGeneralListRes = new GetPostList(userIdx, userLevel, getGeneralList, accessToken);

            return getGeneralListRes;
        }
        catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 정보게시판 리스트 조회 **/
    public GetPostList getInformationList(Long userIdx, String accessToken) throws BaseException {

        try{
            // 사용자의 userLevle 체크
            String userLevel = getUserLevel(userIdx);

            List<GetPostInfo> getInformationList = postDao.selectInformationList(userIdx);

            GetPostList getInformationListRes = new GetPostList(userIdx, userLevel, getInformationList, accessToken);

            return getInformationListRes;
        }
        catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 장터게시판-나눔 리스트 조회 **/
    public GetPostList getShareList(Long userIdx, String accessToken) throws BaseException {

        try{
            // 사용자의 userLevle 체크
            String userLevel = getUserLevel(userIdx);

            List<GetPostInfo> getShareList = postDao.selectShareList(userIdx);

            GetPostList getShareListRes = new GetPostList(userIdx, userLevel, getShareList, accessToken);

            return getShareListRes;
        }
        catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 장터게시판-공동구매 리스트 조회 **/
    public GetPostList getGroupList(Long userIdx, String accessToken) throws BaseException {

        try{
            // 사용자의 userLevle 체크
            String userLevel = getUserLevel(userIdx);

            List<GetPostInfo> getGroupList = postDao.selectGroupList(userIdx);

            GetPostList getGroupListRes = new GetPostList(userIdx, userLevel, getGroupList, accessToken);

            return getGroupListRes;
        }
        catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 장터게시판-중고거래 리스트 조회 **/
    public GetPostList getSecondhandList(Long userIdx, String accessToken) throws BaseException {

        try{
            // 사용자의 userLevle 체크
            String userLevel = getUserLevel(userIdx);

            List<GetPostInfo> getSecondhandList = postDao.selectSecondhandList(userIdx);

            GetPostList getSecondhandListRes = new GetPostList(userIdx, userLevel, getSecondhandList, accessToken);

            return getSecondhandListRes;
        }
        catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 내가 작성한 글 조회 **/
    public GetPostList getMypostList(Long userIdx, String accessToken) throws BaseException {

        try{
            // 사용자의 userLevle 체크
            String userLevel = getUserLevel(userIdx);

            List<GetPostInfo> getMypostList = postDao.selectMypostList(userIdx);

            GetPostList getMypostListRes = new GetPostList(userIdx, userLevel, getMypostList, accessToken);

            return getMypostListRes;
        }
        catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 내가 스크랩한 글 조회 **/
    public GetPostList getMysaveList(Long userIdx, String accessToken) throws BaseException {

        try{
            // 사용자의 userLevle 체크
            String userLevel = getUserLevel(userIdx);

            List<GetPostInfo> getMysaveList = postDao.selectMysaveList(userIdx);

            GetPostList getMysaveListRes = new GetPostList(userIdx, userLevel, getMysaveList, accessToken);

            return getMysaveListRes;
        }
        catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 내가 좋아요한 글 조회 **/
    public GetPostList getMylikeList(Long userIdx, String accessToken) throws BaseException {

        try{
            // 사용자의 userLevle 체크
            String userLevel = getUserLevel(userIdx);

            List<GetPostInfo> getMysaveList = postDao.selectMylikeList(userIdx);

            GetPostList getMylikeListRes = new GetPostList(userIdx, userLevel, getMysaveList, accessToken);

            return getMylikeListRes;
        }
        catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
