package com.example.demo.src.user;


import com.example.demo.config.BaseException;

import com.example.demo.config.BaseResponse;
import com.example.demo.src.auth.*;
import com.example.demo.src.auth.model.PostLoginRes;
import com.example.demo.src.auth.model.UserInfo;
import com.example.demo.src.post.PostProvider;
import com.example.demo.src.post.model.PostSaveRes;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
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
import java.lang.Long;
import java.math.BigInteger;


import javax.sql.DataSource;
import java.lang.*;
import java.sql.Connection;
import java.sql.SQLException;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final AuthDao authDao;
    private final UserProvider userProvider;
    private final PostProvider postProvider;
    private final JwtService jwtService;
    PlatformTransactionManager transactionManager;
    private DataSource dataSource;


    @Autowired
    public UserService(UserDao userDao, AuthDao authDao, UserProvider userProvider, PostProvider postProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.authDao = authDao;
        this.userProvider = userProvider;
        this.postProvider = postProvider;
        this.jwtService = jwtService;

    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /** 관리자 일반 회원가입 **/
    @Transactional
    public PostUserManagerRes createManagerReq(PostUserManagerReq postUserManagerReq) throws BaseException {

        // 이메일 중복 확인 - User, UserRequest
        if(userProvider.checkUserEmail(postUserManagerReq.getEmail()) == 1){
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }

        String pwd;
        try{
            // 비밀번호 암호화
            pwd = new SHA256().encrypt(postUserManagerReq.getPassword());
            postUserManagerReq.setPassword(pwd);
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        // 초대코드 생성
        int length = 15;
        boolean useLetters = true;
        boolean useNumbers = true;
        String inviteCode = RandomStringUtils.random(length, useLetters, useNumbers);
        // 초대코드 중복 확인
        while(userProvider.checkInviteCode(inviteCode) == 1){
            inviteCode = RandomStringUtils.random(length, useLetters, useNumbers);
        }
        System.out.println(inviteCode); //

        // 사용자의 userLevle
        String userLevel = "Manager";

        try{
            // Post - Building
            // name, address, inviteCode
            int buildingIdx = userDao.postBuilding(postUserManagerReq, inviteCode);
            System.out.println("building : "+buildingIdx);

            // Post - User
            // buildingIdx, name, phoneNum, email, password, signupType
            Long userIdx = userDao.postUserManager(postUserManagerReq, buildingIdx);
            System.out.println("userRequest : "+userIdx);

            String accessToken = jwtService.createAccessToken(userIdx);
            String refreshToken = jwtService.createRefreshToken(userIdx);

            // Update - User
            // refreshToken
            userDao.SaveRefeshTokenUserManager(userIdx, refreshToken);

            // 추가된 유저인덱스, 건물 인덱스 반환
            return new PostUserManagerRes(userIdx, userLevel, buildingIdx, accessToken, refreshToken);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 주민 일반 회원가입 **/
    @Transactional
    public PostUserResidentRes createResidentReq(PostUserResidentReq postUserResidentReq) throws BaseException {

        // 이메일 중복 확인 - User, UserRequest
        if(userProvider.checkUserEmail(postUserResidentReq.getEmail()) == 1){
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }

        String pwd;
        try{
            // 비밀번호 암호화
            pwd = new SHA256().encrypt(postUserResidentReq.getPassword());
            postUserResidentReq.setPassword(pwd);
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        // 초대코드 존재확인
        if(userDao.isExistInviteCode(postUserResidentReq.getInviteCode()) == 0){
            throw new BaseException(INVALID_INVITECODE);
        }

        // 사용자의 userLevle
        String userLevel = "Resident";

        try{
            // Get - Building
            // buildingIdx
            int buildingIdx = userProvider.getBuildingIdx(postUserResidentReq.getInviteCode());
            System.out.println("buildingIdx : "+buildingIdx);

            // Post - User
            // buildingIdx, name, phoneNum, email, password, signupType
            Long userIdx = userDao.postUserResident(buildingIdx, postUserResidentReq);
            System.out.println("userIdx : "+userIdx);

            String accessToken = jwtService.createAccessToken(userIdx);
            String refreshToken = jwtService.createRefreshToken(userIdx);

            // Update - User
            // refreshToken
            userDao.SaveRefeshTokenUserManager(userIdx, refreshToken);

            // 추가된 유저요청인덱스, 건물 인덱스 반환
            return new PostUserResidentRes(userIdx, userLevel, accessToken, refreshToken);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 요청 승인 (관리자) **/
    @Transactional
    public UserReqAllowRes userReqAllow(UserReqAllowReq userReqAllowReq, Long userRequestIdx, String accessToken) throws BaseException {

        // 사용자의 userLevel 체크
        String userLevel = postProvider.getUserLevel(userReqAllowReq.getUserIdx());

        try{
            // Patch - User
            // userRequestIdx
            userDao.updateUserReqAllow(userReqAllowReq, userRequestIdx);
            System.out.println("승인된 userRequestIdx : "+userRequestIdx);

            // 추가된 게시글저장 인덱스
            return new UserReqAllowRes(userReqAllowReq.getUserIdx(), userLevel, userRequestIdx, accessToken);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 요청 거절 (관리자) **/
    @Transactional
    public UserReqAllowRes userReqRefuse(Long userIdx, Long userRequestIdx, String accessToken) throws BaseException {

        // 사용자의 userLevel 체크
        String userLevel = postProvider.getUserLevel(userIdx);

        try{
            // Patch - User
            // userRequestIdx
            userDao.updateUserReqRefuse(userRequestIdx);
            System.out.println("거절된 userRequestIdx : "+userRequestIdx);

            // 추가된 게시글저장 인덱스
            return new UserReqAllowRes(userIdx, userLevel, userRequestIdx, accessToken);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 이사 (회원 주소 이동) **/
    @Transactional
    public UserMoveRes userMove(UserMoveReq userMoveReq, String accessToken) throws BaseException {

        // 사용자의 userLevel 체크
        String userLevel = postProvider.getUserLevel(userMoveReq.getUserIdx());

        // 초대코드 존재확인
        if(userDao.isExistInviteCode(userMoveReq.getInviteCode()) == 0){
            throw new BaseException(INVALID_INVITECODE);
        }

        try{
            // Patch - User
            // inviteCode, dong, ho
            int buildingIdx = userDao.updateUserMove(userMoveReq);

            // 추가된 게시글저장 인덱스
            return new UserMoveRes(userMoveReq.getUserIdx(), userLevel, buildingIdx, accessToken);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 회원탈퇴 **/
    @Transactional
    public UserWithdrawalRes UserWithdrawal(UserWithdrawalReq userWithdrawalReq) throws BaseException {
        // 탈퇴하려는 회원의 비밀번호 가져옴
        String userPwd = userDao.getUserPwd(userWithdrawalReq.getUserIdx());
        String encryptPwd;

        try{
            // 입력받은 비밀번호 암호화 -> SHA256
            encryptPwd = new SHA256().encrypt(userWithdrawalReq.getPassword());
        }
        catch (Exception exception){
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        // 저장되어있는 비밀번호와 입력받은 비밀번호 비교
        if(!userPwd.equals(encryptPwd)){
            // 비밀번호 일치하지 않을 경우
            throw new BaseException(NOT_EQUAL_PASSWORD);
        }

        try{
            // 비교를 해주고, 이상이 없다면 탈퇴 진행
            // Patch - User
            userDao.updateUserWithdrawal(userWithdrawalReq);

            return new UserWithdrawalRes(userWithdrawalReq.getUserIdx(), userWithdrawalReq.getReason());
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 프로필 사진 수정 **/
    @Transactional
    public UserProfileImgRes PatchProfileImg(Long userIdx, String imgPath, String accessToken) throws BaseException {
        // 사용자의 userLevel 체크
        String userLevel = postProvider.getUserLevel(userIdx);

        try{
            // Patch - User
            userDao.updateprofileImg(userIdx, imgPath);

            return new UserProfileImgRes(userIdx, userLevel, imgPath, accessToken);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
