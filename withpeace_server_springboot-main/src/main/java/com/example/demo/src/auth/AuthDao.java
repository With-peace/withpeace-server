package com.example.demo.src.auth;

import com.example.demo.src.auth.model.PostLoginReq;
import com.example.demo.src.auth.model.*;
import com.example.demo.src.user.model.PostUserManagerReq;
import com.example.demo.src.user.model.PostUserResidentReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigInteger;
import java.util.List;

@Repository
public class AuthDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /** 일반 로그인 - 유저 정보 조회 **/
    public UserInfo getUserInfo(PostLoginReq postLoginReq){
        // Get - User
        // userIdx, password
        String getUserInfoQuery = "select userIdx, password from User where email=? and status='ACTIVE'";
        String getUserInfoParamstParams = postLoginReq.getEmail();

        return this.jdbcTemplate.queryForObject(getUserInfoQuery, // 리스트면 query, 리스트가 아니면 queryForObject
                (rs,rowNum) -> new UserInfo(
                        rs.getLong("userIdx"),
                        rs.getString("password")
                ), getUserInfoParamstParams);
    }

    /** userIdx 중복확인 - User **/
    public int checkUserIdx(long userIdx){
        String checkUserIdxQuery = "select exists(select userIdx from User where userIdx = ? and status='ACTIVE' and reqStatus='Request' or 'Approve')";
        long checkUserIdxParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserIdxQuery,
                int.class,
                checkUserIdxParams);

    }


    /** 카카오 관리자 회원가입 - Building **/
    public int postBuilding(PostKakaoUserManagerReq postKakaoUserManagerReq, String inviteCode){
        // Post - Building
        // name, address, inviteCode
        String createBuildingQuery = "insert into Building (name, address, inviteCode) VALUES (?,?,?)";
        Object[] createBuildingParams = new Object[]{
                postKakaoUserManagerReq.getBuildingName(),
                postKakaoUserManagerReq.getAddress(),
                inviteCode
        };
        this.jdbcTemplate.update(createBuildingQuery, createBuildingParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }


    /** 카카오 관리자 회원가입 - User **/
    public Long postUserManager(PostKakaoUserManagerReq postKakaoUserManagerReq, int buildingIdx){
        // Post - UserRequest
        // name, phoneNum, email, password, signupType, userLevel
        String createUserRequestQuery = "insert into User (userIdx, buildingIdx, name, phoneNum, signupType, userLevel) VALUES (?,?,?,?,?,?,?)";
        String signupType = "Kakao";
        String userLevel = "Manager";

        Object[] createUserRequestParams = new Object[]{
                postKakaoUserManagerReq.getUserIdx(),
                buildingIdx,
                postKakaoUserManagerReq.getName(),
                postKakaoUserManagerReq.getPhoneNum(),
                signupType,
                userLevel};

        this.jdbcTemplate.update(createUserRequestQuery, createUserRequestParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,Long.class);
    }


    /** 카카오 주민 회원가입 - User **/
    public Long postUserResident(PostKakaoUserResidentReq postKakaoUserResidentReq, int buildingIdx){
        // Post - User
        // userIdx, buildingIdx, name, phoneNum, email, password, signupType, userLevel
        String createUserRequestQuery = "insert into User (userIdx, buildingIdx, name, phoneNum, dong, ho, signupType, userLevel) VALUES (?,?,?,?,?,?,?,?)";
        String signupType = "Kakao";
        String userLevel = "Resident";

        Object[] createUserRequestParams = new Object[]{
                postKakaoUserResidentReq.getAccessToken(),
                buildingIdx,
                postKakaoUserResidentReq.getName(),
                postKakaoUserResidentReq.getPhoneNum(),
                postKakaoUserResidentReq.getDong(),
                postKakaoUserResidentReq.getHo(),
                signupType,
                userLevel};

        this.jdbcTemplate.update(createUserRequestQuery, createUserRequestParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,Long.class);
    }


    /** 카카오 리프레시 토큰 저장 - User **/
    public void saveRefreshToken(Long userIdx, String refreshToken){
        // Update - User
        // userIdx, refreshToken
        String createUserRequestQuery = "update User set refreshToken = ? where userIdx = ? ";

        Object[] createUserRequestParams = new Object[]{
                refreshToken, userIdx};

        this.jdbcTemplate.update(createUserRequestQuery, createUserRequestParams);

    }

    /** 리프레시 토큰 조회 **/
    public String getRefreshToken(Long userIdx){
        // Update - User
        // userIdx, refreshToken
        String getRefreshTokenQuery = "select refreshToken from User where userIdx = ? ";

        Object[] getRefreshTokenParams = new Object[]{userIdx};

        return this.jdbcTemplate.queryForObject(getRefreshTokenQuery,
                String.class,
                getRefreshTokenParams);

    }

    public void logOut(Long userIdx){
        String modifyUserNameQuery = "update User set refreshToken = null where userIdx = ? ";
        Object[] modifyUserNameParams = new Object[]{userIdx};

        this.jdbcTemplate.update(modifyUserNameQuery,modifyUserNameParams);
    }

}
