package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /** 관리자 회원가입 - UserRequest **/
    public int postUserManager(PostUserManagerReq postUserManagerReq){
        // Post - UserRequest
        // name, phoneNum, email, password, signupType, userLevel
        String createUserRequestQuery = "insert into UserRequest (name, phoneNum, email, password, signupType, userLevel) VALUES (?,?,?,?,?,?)";
        String userLevel = "Manager";
//        Object[] createUserRequestParams = new Object[]{
//                postUserManagerReq.getName(),
//                postUserManagerReq.getPhoneNum(),
//                postUserManagerReq.getEmail(),
//                postUserManagerReq.getPassword(),
//                postUserManagerReq.getSignupType(),
//                userLevel};

        // 이메일 주소의 @는 MySQL에서 SYNTAX 오류를 발생시킴
        // @뒤로 작은 따옴표를 붙여 스트링으로 들어가도록 해야함
        // ex) “sj1234’+’@naver.com’
        String email = postUserManagerReq.getEmail();
        int cut = 0;
        for(int i=0; i<email.length(); i++){
            if(email.charAt(i) == '@'){
                cut = i;
                break;
            }
        }
        String email1 = email.substring(0, cut);
        String email2 = email.substring(cut);

        Object[] createUserRequestParams = new Object[]{
                postUserManagerReq.getName(),
                postUserManagerReq.getPhoneNum(),
                email1 + email2,
                postUserManagerReq.getPassword(),
                postUserManagerReq.getSignupType(),
                userLevel};

        this.jdbcTemplate.update(createUserRequestQuery, createUserRequestParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }
    /** 관리자 회원가입 - Building **/
    public int postBuilding(PostUserManagerReq postUserManagerReq, String inviteCode){
        // Post - Building
        // name, address, inviteCode
        String createBuildingQuery = "insert into Building (name, address, inviteCode) VALUES (?,?,?)";
        Object[] createBuildingParams = new Object[]{
                postUserManagerReq.getBuildingName(),
                postUserManagerReq.getAddress(),
                inviteCode
        };
        this.jdbcTemplate.update(createBuildingQuery, createBuildingParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }


    /** 관리자,주민 회원가입 - User 이메일 중복확인 **/
    public int checkUserEmail(String email){
        String checkEmailQuery = "select exists(select email from User where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }
    /** 관리자,주민 회원가입 - UserRequest 이메일 중복확인 **/
    public int checkUserRequestEmail(String email){
        String checkEmailQuery = "select exists(select email from UserRequest where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

    /** 관리자 회원가입 - 초대코드 중복확인 **/
    public int checkInviteCode(String inviteCode){
        String checkInviteCodeQuery = "select exists(select inviteCode from Building where inviteCode = ?)";
        String checkInviteCodeParams = inviteCode;
        return this.jdbcTemplate.queryForObject(checkInviteCodeQuery,
                int.class,
                checkInviteCodeParams);

    }
//
//    public int modifyUserName(PatchUserReq patchUserReq){
//        String modifyUserNameQuery = "update User set nickName = ? where userIdx = ? ";
//        Object[] modifyUserNameParams = new Object[]{patchUserReq.getNickName(), patchUserReq.getUserIdx()};
//
//        return this.jdbcTemplate.update(modifyUserNameQuery,modifyUserNameParams);
//    }




}
