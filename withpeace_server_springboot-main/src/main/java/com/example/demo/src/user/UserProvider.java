package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
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
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
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
    // 이메일 중복확인 - UserRequest
    public int checkUserRequestEmail(String email) throws BaseException{
        try{
            return userDao.checkUserRequestEmail(email);
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



}
