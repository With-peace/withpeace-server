package com.example.demo.utils;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.user.*;
import com.example.demo.src.auth.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class TokenVerify {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JwtService jwtService;
    private final AuthDao authDao;

    @Autowired
    public TokenVerify(JwtService jwtService, AuthDao authDao) {
        this.jwtService = jwtService;
        this.authDao = authDao;
    }

    public String checkToken(Long userIdx) throws BaseException {

        String accessToken = jwtService.getAccessToken();
        String refreshToken = jwtService.getRefreshToken();

        String returnAccessToken = accessToken;

        if(refreshToken == null){
            // access token의 만료시간이 1분 이상 남았을 때
            Long userIdxByJwt = jwtService.getUserIdxAccessToken();
            if(userIdx != userIdxByJwt){
                throw new BaseException(INVALID_USER_JWT);
            }
        }
        else if(refreshToken != null){
            // access token의 만료시간이 만료되었거나 1분이하로 남았을 때
            Long userIdxByAccessJwt = jwtService.getUserIdxAccessToken();
            if(userIdx != userIdxByAccessJwt){
                System.out.println("userIdx:"+userIdx+" access_userIdx:"+userIdxByAccessJwt);
                throw new BaseException(INVALID_USER_JWT);
            }
            Long userIdxByRefreshJwt = jwtService.getUserIdxRefreshToken();
            if(userIdx != userIdxByRefreshJwt){
                throw new BaseException(BaseResponseStatus.INVALID_USER_JWT);
            }

            if(jwtService.isExpired("refresh")){
                // refresh token을 받았을 경우, 토큰의 만료여부 확인
                // 만료된 경우 로그아웃 로직 실행 (DB에서 refresh toekn 삭제, 로그아웃 메시지 출력)
                authDao.logOut(userIdx);
                throw new BaseException(BaseResponseStatus.ISEXPIRED_REFRESH_TOKEN);
            }

            // access token의 재발행 실행
            // DB에서 userIdx인 refresh token을 가져옴
            String db_refreshToken = authDao.getRefreshToken(userIdx);
            // HEADER에서 refresh token을 가져옴
            String h_refreshToken = jwtService.getRefreshToken();

            if(db_refreshToken.equals(h_refreshToken)){
                // 동일한지 비교, 동일한 경우 access token 재발행
                returnAccessToken = jwtService.createAccessToken(userIdx);
            } else{
                // 동일하지 않을 경우, 로그아웃 로직 실행 (DB에서 refresh token 삭제, 로그아웃 메시지 출력)
                authDao.logOut(userIdx);
                throw new BaseException(NOTEQUALS_DB_HEADER_REFRESH_TOKEN);
            }
        }

        return returnAccessToken;
    }

}
