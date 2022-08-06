package com.example.demo.src.user;


import com.example.demo.config.BaseException;

import com.example.demo.config.BaseResponse;
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
    private final UserProvider userProvider;
    private final JwtService jwtService;
    PlatformTransactionManager transactionManager;
    private DataSource dataSource;


    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
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

        try{
            // Post - Building
            // name, address, inviteCode
            int buildingIdx = userDao.postBuilding(postUserManagerReq, inviteCode);
            System.out.println("building : "+buildingIdx);

            // Post - User
            // buildingIdx, name, phoneNum, email, password, signupType
            Long userIdx = userDao.postUserManager(postUserManagerReq, buildingIdx);
            System.out.println("userRequest : "+userIdx);

            String jwt = jwtService.createJwt(userIdx);

            // 추가된 유저인덱스, 건물 인덱스 반환
            return new PostUserManagerRes(userIdx, buildingIdx, jwt);
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

        try{
            // Get - Building
            // buildingIdx
            int buildingIdx = userProvider.getBuildingIdx(postUserResidentReq.getInviteCode());
            System.out.println("buildingIdx : "+buildingIdx);

            // Post - User
            // buildingIdx, name, phoneNum, email, password, signupType
            Long userIdx = userDao.postUserResident(buildingIdx, postUserResidentReq);
            System.out.println("userIdx : "+userIdx);

            String jwt = jwtService.createJwt(userIdx);

            // 추가된 유저요청인덱스, 건물 인덱스 반환
            return new PostUserResidentRes(userIdx, jwt);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


}
