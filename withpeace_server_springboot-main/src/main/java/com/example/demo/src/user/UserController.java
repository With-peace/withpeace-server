package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
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


    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService) {
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
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
        // 휴대폰 번호 인증하지 않았을 때
        if (postUserManagerReq.getPhoneNumCheck().equals("T") == false || postUserManagerReq.getPhoneNumCheck() == null) {
            System.out.println("휴대폰 인증 필요");
            return new BaseResponse<>(POST_USERS_INVALID_PHONENUM);
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
            if(userProvider.checkUserEmail(email.get("email")) == 1
                    || userProvider.checkUserRequestEmail(email.get("email")) == 1){
                getEmailCheck.setCheckEmail("T");
            }
            return new BaseResponse<>(getEmailCheck);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


}
