package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.user.model.*;
import com.example.demo.src.auth.*;
import com.example.demo.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;

import java.lang.*;
import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final AuthDao authDao;
    @Autowired
    private final TokenVerify tokenVerify;


    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService, AuthDao authDao, TokenVerify tokenVerify) {
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
        this.authDao = authDao;
        this.tokenVerify = tokenVerify;
    }

    /**
     * 관리자 일반 회원가입 API
     * [POST] /users/signup/manager
     *
     * @return BaseResponse<PostUserManagerRes>
     */
    @ResponseBody
    @PostMapping("/signup/manager")
    public BaseResponse<PostUserManagerRes> createManagerRequest(@RequestBody PostUserManagerReq postUserManagerReq) {

        // 이름 입력하지 않았을 때
        if (postUserManagerReq.getName() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        }
        // 휴대폰 번호 입력하지 않았을 때
        if (postUserManagerReq.getPhoneNum() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PHONENUM);
        }
        // 휴대폰 번호 정규표현
        if (!isRegexPhoneNum(postUserManagerReq.getPhoneNum())) {
            return new BaseResponse<>(POST_USERS_INVALID_PHONENUM);
        }
        // 휴대폰 번호 인증하지 않았을 때
        if (postUserManagerReq.getPhoneNumCheck().equals("T") == false || postUserManagerReq.getPhoneNumCheck() == null) {
            System.out.println("휴대폰 인증 필요");
            return new BaseResponse<>(POST_USERS_CHECK_PHONENUM);
        }
        // 이메일 입력하지 않았을 때
        if (postUserManagerReq.getEmail() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        // 이메일 정규표현
        if (!isRegexEmail(postUserManagerReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        // 비밀번호 입력하지 않았을 때
        if (postUserManagerReq.getPassword() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }
        // 비밀번호 정규표현
        if (!isRegexPwd(postUserManagerReq.getPassword())) {
            // 8~16자, 최소 하나의 문자, 하나의 숫자 및 하나의 특수 문자 포함
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        }
        // 주소 입력하지 않았을 때
        if (postUserManagerReq.getAddress() == null) {
            return new BaseResponse<>(POST_USERS_MANAGER_EMPTY_ADDRESS);
        }
        // 건물 이름 입력하지 않았을 때
        if (postUserManagerReq.getBuildingName() == null) {
            return new BaseResponse<>(POST_USERS_MANAGER_EMPTY_BUILDINGNAME);
        }
        // 정보 이용 동의하지 않았을 때
        if (postUserManagerReq.getAgreeInfo().equals("T") == false || postUserManagerReq.getPhoneNumCheck() == null) {
            System.out.println("개인정보 수집 및 이용에 동의 필요");
            return new BaseResponse<>(POST_USERS_EMPTY_AGREEINFO);
        }

        try {
            PostUserManagerRes PostUserManagerRes = userService.createManagerReq(postUserManagerReq);
            return new BaseResponse<>(PostUserManagerRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 주민 일반 회원가입 API
     * [POST] /users/signup/resident
     *
     * @return BaseResponse<PostUserResidentRes>
     */
    @ResponseBody
    @PostMapping("/signup/resident")
    public BaseResponse<PostUserResidentRes> createResidentReq(@RequestBody PostUserResidentReq postUserResidentReq) {

        // 이름 입력하지 않았을 때
        if (postUserResidentReq.getName() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        }
        // 휴대폰 번호 입력하지 않았을 때
        if (postUserResidentReq.getPhoneNum() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PHONENUM);
        }
        // 휴대폰 번호 정규표현
        if (!isRegexPhoneNum(postUserResidentReq.getPhoneNum())) {
            return new BaseResponse<>(POST_USERS_INVALID_PHONENUM);
        }
        // 휴대폰 번호 인증하지 않았을 때
        if (postUserResidentReq.getPhoneNumCheck().equals("T") == false || postUserResidentReq.getPhoneNumCheck() == null) {
            System.out.println("휴대폰 인증 필요");
            return new BaseResponse<>(POST_USERS_CHECK_PHONENUM);
        }
        // 이메일 입력하지 않았을 때
        if (postUserResidentReq.getEmail() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        // 이메일 정규표현
        if (!isRegexEmail(postUserResidentReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        // 비밀번호 입력하지 않았을 때
        if (postUserResidentReq.getPassword() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }
        // 비밀번호 정규표현
        if (!isRegexPwd(postUserResidentReq.getPassword())) {
            // 8~16자, 최소 하나의 문자, 하나의 숫자 및 하나의 특수 문자 포함
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        }
        // 초대코드 입력하지 않았을 때
        if (postUserResidentReq.getInviteCode() == null) {
            return new BaseResponse<>(POST_USERS_MANAGER_EMPTY_ADDRESS);
        }
        // 호수 입력하지 않았을 때
        if (postUserResidentReq.getHo() == null) {
            return new BaseResponse<>(POST_USERS_MANAGER_EMPTY_BUILDINGNAME);
        }
        // 정보 이용 동의하지 않았을 때
        if (postUserResidentReq.getAgreeInfo().equals("T") == false || postUserResidentReq.getAgreeInfo() == null) {
            System.out.println("개인정보 수집 및 이용에 동의 필요");
            return new BaseResponse<>(POST_USERS_EMPTY_AGREEINFO);
        }

        try {
            PostUserResidentRes postUserResidentRes = userService.createResidentReq(postUserResidentReq);
            return new BaseResponse<>(postUserResidentRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }



    /**
     * 회원가입 - 이메일 중복확인 API
     * [GET] /users/signup/checkEmail
     *
     * @return BaseResponse<GetEmailCheck>
     */
    @ResponseBody
    @GetMapping("/signup/checkEmail")
    public BaseResponse<GetEmailCheck> checkEmail(@RequestBody Map<String, String> email) {

        // 이메일 입력하지 않았을 때
        if (email.get("email") == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        // 이메일 정규표현
        if (!isRegexEmail(email.get("email"))) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }

        try {
            GetEmailCheck getEmailCheck = new GetEmailCheck("F");

            // 이메일 중복 확인 - User, UserRequest
            if(userProvider.checkUserEmail(email.get("email")) == 1){
                getEmailCheck.setCheckEmail("T");
            }
            return new BaseResponse<>(getEmailCheck);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 사용자 화면 조회 API
     * [GET] /users/userInfo
     *
     * @return BaseResponse<UserInfoRes>
     */
    @ResponseBody
    @GetMapping("/userInfo")
    public BaseResponse<UserInfoRes> getUserInfo(@RequestBody Map<String, Long> userIdx) throws BaseException {

        // 유저인덱스 입력하지 않았을 때
        if (userIdx.get("userIdx") == null) {
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }

        String first_accessToken = jwtService.getAccessToken();
        // 토큰 검증
        String new_accessToken = tokenVerify.checkToken(userIdx.get("userIdx"));
        String accessToken = null;
        if(first_accessToken != new_accessToken){
            accessToken = new_accessToken;
        }

        try {
            UserInfoRes userInfoRes = userProvider.getUserInfo(userIdx.get("userIdx"), accessToken);
            return new BaseResponse<>(userInfoRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 토큰 테스트 API
     * [GET] /users/tokenTest/{userIdx}
     *
     * @return BaseResponse<GetEmailCheck>
     */
    @ResponseBody
    @GetMapping("/tokenTest/{userIdx}")
    public BaseResponse<String> TokenTest(@PathVariable("userIdx") Long userIdx) throws BaseException {

        String first_accessToken = jwtService.getAccessToken(); // 테스트 위한 코드 (원래 access token)
        // 토큰 검증
        String new_accessToken = tokenVerify.checkToken(userIdx);

        String result = "토큰 테스트//";
        if(first_accessToken != new_accessToken){
            result += "access token 재발급 - ";
            result += "new token : " + new_accessToken;
        }
        else{
            result += "access token 정상 - ";
            result += "first_accessToken : " + first_accessToken;
        }


        return new BaseResponse<>(result);
    }

}
