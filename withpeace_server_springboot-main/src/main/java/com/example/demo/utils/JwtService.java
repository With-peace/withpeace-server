package com.example.demo.utils;


import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.Date;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class JwtService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    /*
    JWT 생성
    @param userIdx
    @return String
     */
    public String createJwt(Long userIdx){
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("type","jwt")
                .claim("userIdx",userIdx)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis()+1*(1000*60*60*24*365)))
                .signWith(SignatureAlgorithm.HS256, Secret.JWT_SECRET_KEY)
                .compact();
    }

    /*
    ACCESS TOEKN(JWT) 생성
    @param userIdx
    @return String
     */
    public String createAccessToken(Long userIdx){
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("type","jwt")
                .claim("userIdx",userIdx)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis()+1*(1000*60*60*2))) // 2시간
                .signWith(SignatureAlgorithm.HS256, Secret.JWT_SECRET_KEY)
                .compact();
    }

    /*
    REFRESH TOEKN(JWT) 생성
    @param userIdx
    @return String
     */

    public String createRefreshToken(Long userIdx){
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("type","jwt")
                .claim("userIdx",userIdx)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis()+1*(1000*60*60*24*14))) // 2주
                .signWith(SignatureAlgorithm.HS256, Secret.JWT_SECRET_KEY)
                .compact();
    }

    /*
    Header에서 X-ACCESS-TOKEN 으로 JWT 추출
    @return String
     */
    public String getAccessToken(){
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("X-ACCESS-TOKEN");
    }

    /*
    Header에서 X-REFRESH-TOKEN 으로 JWT 추출
    @return String
     */
    public String getRefreshToken(){
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("X-REFRESH-TOKEN");
    }

    /*
    AccessToken에서 userIdx 추출
    @return Long
    @throws BaseException
     */
    public Long getUserIdxAccessToken() throws BaseException{
        //1. JWT 추출
        String token = getAccessToken();
        if(token == null || token.length() == 0){
            throw new BaseException(EMPTY_JWT);
        }

        // 2. JWT parsing
        Jws<Claims> claims;
        try{
            claims = Jwts.parser()
                    .setSigningKey(Secret.JWT_SECRET_KEY)
                    .parseClaimsJws(token);
        } catch (Exception ignored) { // Exception ignored
            System.out.println("토큰이 유효하지 않습니다.");
            return null;
//            logger.error("Error!", exception);
//            throw new BaseException(INVALID_JWT);
        }

        // 3. userIdx 추출
        return claims.getBody().get("userIdx",Long.class);
    }

    /*
    RefreshToken에서 userIdx 추출
    @return Long
    @throws BaseException
     */
    public Long getUserIdxRefreshToken() throws BaseException{
        //1. JWT 추출
        String token = getAccessToken();
//        if(token == null || token.length() == 0){
//            throw new BaseException(EMPTY_JWT);
//        }

        // 2. JWT parsing
        Jws<Claims> claims;
        try{
            claims = Jwts.parser()
                    .setSigningKey(Secret.JWT_SECRET_KEY)
                    .parseClaimsJws(token);
        } catch (Exception ignored) {
            System.out.println("토큰이 유효하지 않습니다.");
            return null;
//            throw new BaseException(INVALID_JWT);
        }

        // 3. userIdx 추출
        return claims.getBody().get("userIdx",Long.class);
    }

    /*
    JWT의 만료여부
    @return int
    @throws BaseException
     */
    public boolean isExpired(String token) throws BaseException{
        boolean result = false;

        //1. JWT 추출
        if(token == "access"){ // access token 에서 userIdx 추출
            token = getAccessToken();
            if(token == null || token.length() == 0){
                throw new BaseException(EMPTY_JWT);
            }
        }else if(token == "refresh"){ // refresh token 에서 userIdx 추출
            token = getRefreshToken();
            if(token == null || token.length() == 0){
                throw new BaseException(EMPTY_JWT);
            }
        }

        // 2. JWT parsing
        Jws<Claims> claims;
        try{
            claims = Jwts.parser()
                    .setSigningKey(Secret.JWT_SECRET_KEY)
                    .parseClaimsJws(token);
        } catch (Exception ignored) {
            result = true;
            return result;
//            throw new BaseException(INVALID_JWT);
        }

        Date now = new Date(System.currentTimeMillis()); // 현재시간 (밀리초)
        Date jwtExpiration = claims.getBody().get("exp",Date.class); // toekn 만료시간 (밀리초)


        if(jwtExpiration.before(now)){
            // 갱신 필요
            result = true;
        }

        return result;
    }

    /*
    ACCESS TOKEN 만료시간 갱신
    @return String
    @throws BaseException
     */
    public String updateToken() throws BaseException{
        //1. JWT 추출
        String token = getAccessToken();
        if(token == null || token.length() == 0){
            throw new BaseException(EMPTY_JWT);
        }

        // 2. JWT parsing
        Jws<Claims> claims;
        try{
            claims = Jwts.parser()
                    .setSigningKey(Secret.JWT_SECRET_KEY)
                    .parseClaimsJws(token);
        } catch (Exception ignored) {
            throw new BaseException(INVALID_JWT);
        }

        Date now = new Date();
        claims.getBody().setIssuedAt(now)
                        .setExpiration(new Date(System.currentTimeMillis()+1*(1000*60*60*2))); // 2시간

        return token;

    }

//    /*
//    JWT 만료하기
//    @param jwt
//    @return String
//     */
//    public void deleteJwt() throws BaseException{
//        //1. JWT 추출
//        String accessToken = getJwt();
//        if(accessToken == null || accessToken.length() == 0){
//            throw new BaseException(EMPTY_JWT);
//        }
//
//        // 2. JWT parsing
//        Jws<Claims> claims;
//        try{
//            claims = Jwts.parser()
//                    .setSigningKey(Secret.JWT_SECRET_KEY)
//                    .parseClaimsJws(accessToken);
//        } catch (Exception ignored) {
//            throw new BaseException(INVALID_JWT);
//        }
//
//        // 3. 만료시간 현재 시간으로부터 0.5초 후로 변경
//        System.out.println("현재시간 : "+new Date(System.currentTimeMillis()));
//        System.out.println("토큰만료시간 : "+claims.getBody().getExpiration());
//        System.out.println("토큰 getBody : "+claims.getBody());
//        System.out.println();
//        claims.getBody().setExpiration(new Date(System.currentTimeMillis()+1));
//        System.out.println("토큰만료시간 : "+claims.getBody().getExpiration());
//        System.out.println("토큰 getBody : "+claims.getBody());
//    }
//
//    /*
//    JWT 만료 확인
//    @param jwt
//    @return String
//     */
//    public void checkJwtExp() throws BaseException{
//        //1. JWT 추출
//        String accessToken = getJwt();
//        if(accessToken == null || accessToken.length() == 0){
//            throw new BaseException(EMPTY_JWT);
//        }
//
//        // 2. JWT parsing
//        Jws<Claims> claims;
//        try{
//            claims = Jwts.parser()
//                    .setSigningKey(Secret.JWT_SECRET_KEY)
//                    .parseClaimsJws(accessToken);
//        } catch (Exception exception) {
//            throw  new BaseException(INVALID_JWT);
//        }
//    }
}
