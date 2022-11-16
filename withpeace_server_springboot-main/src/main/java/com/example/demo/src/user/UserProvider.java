package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.post.PostProvider;
import com.example.demo.src.post.model.GetPostRes;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class UserProvider {

    private final UserDao userDao;
    private final PostProvider postProvider;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, PostProvider postProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.postProvider = postProvider;
        this.jwtService = jwtService;
    }
    
    // 이메일 중복확인 - User
    public int checkUserEmail(String email) throws BaseException{
        try{
            return userDao.checkUserEmail(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    
    // 초대코드 중복확인
    public int checkInviteCode(String invideCode) throws BaseException{
        try{
            return userDao.checkInviteCode(invideCode);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 초대코드 확인 - buildingIdx
    public int getBuildingIdx(String inviteCode) throws BaseException{
        try{
            return userDao.getBuildingIdx(inviteCode);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 사용자 화면 조회 **/
    public UserInfoRes getUserInfo(Long userIdx, String accessToken) throws BaseException{
        try{
            // 사용자의 userLevel 체크
            String userLevel = postProvider.getUserLevel(userIdx);

            UserInfoRes userInfoRes = userDao.selectUserInfo(userIdx, userLevel, accessToken);

            return userInfoRes;
        }
        catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 요청 목록 조회 (관리자) **/
    public UserReqListRes getUserReqList(Long userIdx, String accessToken) throws BaseException{
        try{
            // 사용자의 userLevel 체크
            String userLevel = postProvider.getUserLevel(userIdx);

            UserReqListRes userReqListRes = userDao.selectUserReqList(userIdx, userLevel, accessToken);

            return userReqListRes;
        }
        catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }



}
