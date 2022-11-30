package com.example.demo.src.user;


import com.example.demo.src.post.model.GetCommentRes;
import com.example.demo.src.post.model.GetPostImageRes;
import com.example.demo.src.post.model.GetPostInfo;
import com.example.demo.src.post.model.GetPostRes;
import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigInteger;
import java.lang.Long;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;
    private List<UserReqList> UserReqList;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /** 관리자 일반 회원가입 - UserRequest **/
    public Long postUserManager(PostUserManagerReq postUserManagerReq, int buildingIdx){
        // Post - UserRequest
        // name, phoneNum, email, password, signupType, userLevel
        String createUserRequestQuery = "insert into User (buildingIdx, name, phoneNum, email, password, signupType, userLevel) VALUES (?,?,?,?,?,?,?)";
        String signupType = "General";
        String userLevel = "Manager";

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
                buildingIdx,
                postUserManagerReq.getName(),
                postUserManagerReq.getPhoneNum(),
                email1 + email2,
                postUserManagerReq.getPassword(),
                signupType,
                userLevel};

        this.jdbcTemplate.update(createUserRequestQuery, createUserRequestParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,Long.class);
    }
    /** 관리자 일반 회원가입 - Building **/
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


    /** 주민 일반 회원가입 - Building **/
    // 해당하는 초대코드의 buildingIdx를 가져오는 sql
    public int getBuildingIdx(String inviteCode){
        // Get - Building
        // buildingIdx
        String getBuildingIdxQuery = "" +
                "select buildingIdx\n" +
                "from Building\n" +
                "where inviteCode=?";
        String getBuildingIdxParams = inviteCode;

        return this.jdbcTemplate.queryForObject(getBuildingIdxQuery, // 리스트면 query, 리스트가 아니면 queryForObject
                (rs,rowNum) -> rs.getInt("buildingIdx"), getBuildingIdxParams);
    }

    /** 주민 일반 회원가입 - User **/
    public Long postUserResident(int buildingIdx, PostUserResidentReq postUserResidentReq){
        // Post - UserRequest
        // buildingIdx, name, phoneNum, email, password, dong, ho, signupType
        String createUserRequestQuery = "insert into User (buildingIdx, name, phoneNum, email, password, dong, ho, signupType, userLevel) VALUES (?,?,?,?,?,?,?,?,?)";
        String signupType = "General";
        String userLevel = "Resident";

        // 이메일 주소의 @는 MySQL에서 SYNTAX 오류를 발생시킴
        // @뒤로 작은 따옴표를 붙여 스트링으로 들어가도록 해야함
        // ex) “sj1234’+’@naver.com’
        String email = postUserResidentReq.getEmail();
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
                buildingIdx,
                postUserResidentReq.getName(),
                postUserResidentReq.getPhoneNum(),
                email1 + email2,
                postUserResidentReq.getPassword(),
                postUserResidentReq.getDong(),
                postUserResidentReq.getHo(),
                signupType,
                userLevel};

        this.jdbcTemplate.update(createUserRequestQuery, createUserRequestParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,Long.class);
    }


    /** 관리자,주민 회원가입 - User 이메일 중복확인 **/
    public int checkUserEmail(String email){
        String checkEmailQuery = "select exists(select email from User where email = ? and status='ACTIVE' and reqStatus='Request' or 'Approve')";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

//    /** 관리자,주민 회원가입 - UserRequest 이메일 중복확인 **/
//    public int checkUserRequestEmail(String email){
//        String checkEmailQuery = "select exists(select email from UserRequest where email = ? and status='Request' or 'Approve')";
//        String checkEmailParams = email;
//        return this.jdbcTemplate.queryForObject(checkEmailQuery,
//                int.class,
//                checkEmailParams);
//
//    }

    /** 관리자 회원가입 - 초대코드 중복확인 **/
    public int checkInviteCode(String inviteCode){
        String checkInviteCodeQuery = "select exists(select inviteCode from Building where inviteCode = ?)";
        String checkInviteCodeParams = inviteCode;
        return this.jdbcTemplate.queryForObject(checkInviteCodeQuery,
                int.class,
                checkInviteCodeParams);

    }

    /** 주민 회원가입 - 초대코드 존재확인 **/
    public int isExistInviteCode(String inviteCode){
        String isExistInviteCodeQuery = "select exists(select inviteCode from Building where inviteCode = ? and status = 'Approve')";
        String isExistInviteCodeParams = inviteCode;
        return this.jdbcTemplate.queryForObject(isExistInviteCodeQuery, int.class,
                isExistInviteCodeParams);
    }

    /** 사용자 화면 조회 **/
    public UserInfoRes selectUserInfo(Long userIdx, String userLevel, String accessToken){
        String selectUserInfoQuery =
                "select userIdx, userLevel, name, dong, ho, profileImgUrl as profileImg\n" +
                "from User\n" +
                "where userIdx=?";
        Long selectUserInfoParam = userIdx;
        return this.jdbcTemplate.queryForObject(selectUserInfoQuery, // 리스트면 query, 리스트가 아니면 queryForObject
                (rs,rowNum) -> new UserInfoRes(
                        userIdx, // 현재 사용자
                        userLevel,
                        rs.getString("name"),
                        rs.getInt("dong"),
                        rs.getInt("ho"),
                        rs.getString("profileImg"),
                        accessToken
                ), selectUserInfoParam);
    }

    /** 요청 목록 조회 (관리자) **/
    public UserReqListRes selectUserReqList(Long userIdx, String userLevel, String accessToken){
        // 사용자의 buildingIdx 조회
        String selectBuildingIdxQuery =
                "select buildingIdx\n" +
                        "from User\n" +
                        "where userIdx=?";
        int buildingIdx = this.jdbcTemplate.queryForObject(selectBuildingIdxQuery,
                (rs,rowNum) -> rs.getInt("buildingIdx"), userIdx);
        
        // 해당하는 buildingIdx의 주민 요청 목록 조회
        String selectUserReqListQuery =
                "select userIdx as userRequestIdx, name, dong, ho, phoneNum\n" +
                "from User\n" +
                "where buildingIdx=? and reqStatus='Request' and status='ACTIVE'";
        return new UserReqListRes(
                userIdx,
                userLevel,
                UserReqList = this.jdbcTemplate.query(selectUserReqListQuery,
                                (rk, rownum) -> new UserReqList(
                                        rk.getLong("userRequestIdx"),
                                        rk.getString("name"),
                                        rk.getInt("dong"),
                                        rk.getInt("ho"),
                                        rk.getString("phoneNum")
                                ), buildingIdx),
                accessToken
        );

    }

    /** 요청 승인 (관리자) **/
    public void updateUserReqAllow(UserReqAllowReq userReqAllowReq, Long userRequestIdx){
        String updateUserReqQuery = "UPDATE User SET dong=?, ho=?, reqStatus='Approve' WHERE userIdx=?;";
        Object[] updateUserReqParams = new Object[]{
                userReqAllowReq.getDong(),
                userReqAllowReq.getHo(),
                userRequestIdx};

        this.jdbcTemplate.update(updateUserReqQuery, updateUserReqParams);
    }

    /** 요청 거절 (관리자) **/
    public void updateUserReqRefuse(Long userRequestIdx){
        String updateUserReqQuery = "UPDATE User SET reqStatus='Refuse' WHERE userIdx=?;";
        Long updateUserReqParams = userRequestIdx;

        this.jdbcTemplate.update(updateUserReqQuery, updateUserReqParams);
    }


    public void SaveRefeshTokenUserManager(Long userIdx, String refreshToken){
        String modifyUserNameQuery = "update User set refreshToken = ? where userIdx = ? ";
        Object[] modifyUserNameParams = new Object[]{refreshToken, userIdx};

        this.jdbcTemplate.update(modifyUserNameQuery,modifyUserNameParams);
    }




}
