package com.example.demo.src.auth;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.auth.model.*;
import com.example.demo.src.auth.model.SendSmsResponseDto;
import com.example.demo.src.user.model.PostUserResidentReq;
import com.example.demo.src.user.model.PostUserResidentRes;
import com.example.demo.utils.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.*;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final AuthProvider authProvider;
    @Autowired
    private final AuthService authService;
    @Autowired
    private final JwtService jwtService;


    public AuthController(AuthProvider authProvider, AuthService authService, JwtService jwtService){
        this.authProvider = authProvider;
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @PostMapping("/phoneAuth")
    public BaseResponse<SendSmsResponseDto> phoneAuth(@RequestParam String phoneNum, HttpServletRequest request) throws UnsupportedEncodingException, ParseException, NoSuchAlgorithmException, URISyntaxException, InvalidKeyException, JsonProcessingException {

        Random rand = new Random();
        String numStr = "";
        for (int i = 0; i < 6; i++) { // 랜덤 6자리 숫자
            String ran = Integer.toString(rand.nextInt(10));
            numStr += ran;
        }
        String content = "[WITHPEACE] 회원가입 인증번호 "+numStr+" 를 입력해주세요.";

        // 세션에 인증번호 저장
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(120); // 세션 만료시간 설청 (초단위)
        session.setAttribute("rand", numStr);

        SendSmsResponseDto sendSmsResponseDto = authService.sendSms(phoneNum, content);
        return new BaseResponse<>(sendSmsResponseDto);

    }

    @ResponseBody
    @PostMapping("/phoneAuthOk")
    public BaseResponse<String> phoneAuthOk(HttpServletRequest request, @RequestBody Map<String, String> code) {
        // 랜덤 인증번호는 세션으로 받고, 입력번호는 body로 받음
        HttpSession session = request.getSession();
        if(session.getMaxInactiveInterval() > 120){
            // 세션이 만료되었을 경우
            System.out.println("세션 만료");
            return new BaseResponse<>(POST_AUTH_SESSION_TIMEOUT);
        }
        String rand = (String) session.getAttribute("rand");
        String num = code.get("code");

        System.out.println(rand + " : " + num);

        if (rand.equals(num)) {
            session.removeAttribute("rand");
            return new BaseResponse<>("T");
        }
        else{
            return new BaseResponse<>("F");
        }
    }

//    /**
//     * 카카오 callback
//     * [GET] /auth/kakao/callback
//     * 백엔드 테스트
//     */
//    @ResponseBody
//    @GetMapping("/kakao")
//    public BaseResponse<KakaoUserInfo> kakaoCallback(@RequestParam String code) {
//
//        // 클라이언트에게 받은 인가코드로 ACCESS_TOKEN 가져오기
//        String access_token = authService.getKakaoAccessToken(code);
//
//        // ACCESS_TOKEN을 통해 사용자 정보 가져오기
//        KakaoUserInfo kakaoUserInfo = authService.getKakaoUserInfo(access_token);
//
//        return new BaseResponse<>(kakaoUserInfo);
//    }

    /**
     * 카카오 사용자 정보 가져오기
     * [GET] /auth/kakao
     */
    @ResponseBody
    @GetMapping("/kakao")
    public BaseResponse<KakaoCallbackRes> kakaoCallback(@RequestParam String accessToken) {
        try{
            // ACCESS_TOKEN을 통해 사용자 정보 가져오기
            KakaoUserInfo kakaoUserInfo = authService.getKakaoUserInfo(accessToken);

            // 다움단계 - Login or Signup
            String nextLevel;

            // id 중복확인
            if(authProvider.checkUserIdx(kakaoUserInfo.getId()) == 1){
                // 로그인
                nextLevel = "Login";
            } else{
                nextLevel = "Signup";

            }
            KakaoCallbackRes kakaoCallbackRes = new KakaoCallbackRes(kakaoUserInfo.getId(), kakaoUserInfo.getNickname(), accessToken, nextLevel);
            return new BaseResponse<>(kakaoCallbackRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 카카오 로그인 API
     * [POST] /auth/kakao/login
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/kakao/login")
    public BaseResponse<PostLoginRes> kakaoLogin(@RequestBody KakaoCallbackRes kakaoCallbackRes) {
        PostLoginRes postLoginRes = new PostLoginRes(kakaoCallbackRes.getUserIdx(), kakaoCallbackRes.getAccessToken());
        return new BaseResponse<>(postLoginRes);
    }


    /**
     * 관리자 카카오 회원가입
     * [POST] /auth/kakao/signup/manager
     */
    @ResponseBody
    @GetMapping("/kakao/signup/manager")
    public BaseResponse<PostKakaoUserManagerRes> kakaoUserManagerReq(@RequestBody PostKakaoUserManagerReq postKakaoUserManagerReq) {

        // 이름 입력하지 않았을 때
        if (postKakaoUserManagerReq.getName() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        }
        // 휴대폰 번호 입력하지 않았을 때
        if (postKakaoUserManagerReq.getPhoneNum() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PHONENUM);
        }
        // 휴대폰 번호 정규표현
        if (!isRegexPhoneNum(postKakaoUserManagerReq.getPhoneNum())) {
            return new BaseResponse<>(POST_USERS_INVALID_PHONENUM);
        }
        // 휴대폰 번호 인증하지 않았을 때
        if (postKakaoUserManagerReq.getPhoneNumCheck().equals("T") == false || postKakaoUserManagerReq.getPhoneNumCheck() == null) {
            System.out.println("휴대폰 인증 필요");
            return new BaseResponse<>(POST_USERS_CHECK_PHONENUM);
        }
        // 주소 입력하지 않았을 때
        if (postKakaoUserManagerReq.getAddress() == null) {
            return new BaseResponse<>(POST_USERS_MANAGER_EMPTY_ADDRESS);
        }
        // 건물 이름 입력하지 않았을 때
        if (postKakaoUserManagerReq.getBuildingName() == null) {
            return new BaseResponse<>(POST_USERS_MANAGER_EMPTY_BUILDINGNAME);
        }
        // 정보 이용 동의하지 않았을 때
        if (postKakaoUserManagerReq.getAgreeInfo().equals("T") == false || postKakaoUserManagerReq.getPhoneNumCheck() == null) {
            System.out.println("개인정보 수집 및 이용에 동의 필요");
            return new BaseResponse<>(POST_USERS_EMPTY_AGREEINFO);
        }

        try{
            PostKakaoUserManagerRes postKakaoUserManagerRes = authService.createManagerReq(postKakaoUserManagerReq);
            return new BaseResponse<>(postKakaoUserManagerRes);

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 주민 카카오 회원가입
     * [POST] /auth/kakao/signup/resident
     */
    @ResponseBody
    @PostMapping("/kakao/signup/resident")
    public BaseResponse<PostKakaoUserResidentRes> kakaoUserResidentReq(@RequestBody PostKakaoUserResidentReq postKakaoUserResidentReq) {

        // 이름 입력하지 않았을 때
        if (postKakaoUserResidentReq.getName() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        }
        // 휴대폰 번호 입력하지 않았을 때
        if (postKakaoUserResidentReq.getPhoneNum() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PHONENUM);
        }
        // 휴대폰 번호 정규표현
        if (!isRegexPhoneNum(postKakaoUserResidentReq.getPhoneNum())) {
            return new BaseResponse<>(POST_USERS_INVALID_PHONENUM);
        }
        // 초대코드 입력하지 않았을 때
        if (postKakaoUserResidentReq.getInviteCode() == null) {
            return new BaseResponse<>(POST_USERS_MANAGER_EMPTY_ADDRESS);
        }
        // 호수 입력하지 않았을 때
        if (postKakaoUserResidentReq.getHo() == null) {
            return new BaseResponse<>(POST_USERS_MANAGER_EMPTY_BUILDINGNAME);
        }
        // 정보 이용 동의하지 않았을 때
        if (postKakaoUserResidentReq.getAgreeInfo().equals("T") == false || postKakaoUserResidentReq.getAgreeInfo() == null) {
            System.out.println("개인정보 수집 및 이용에 동의 필요");
            return new BaseResponse<>(POST_USERS_EMPTY_AGREEINFO);
        }

        try {
            PostKakaoUserResidentRes postKakaoUserResidentRes = authService.createResidentReq(postKakaoUserResidentReq);
            return new BaseResponse<>(postKakaoUserResidentRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 일반 로그인 API
     * [POST] /auth/login
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/login")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) {
        try{
            // 이메일 입력하지 않았을 때
            if(postLoginReq.getEmail() == null) {
                return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_EMAIL);
            }
            // 비밀번호를 입력하지 않았을 때
            if(postLoginReq.getPassword() == null) {
                return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_PASSWORD);
            }

            if(!isRegexEmail(postLoginReq.getEmail())){ // 이메일 형식 확인
                return new BaseResponse<>(BaseResponseStatus.POST_USERS_INVALID_EMAIL);
            }

            PostLoginRes postLoginRes = authService.LogIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 로그아웃 API
     * [POST] /auth/logout/{userIdx}
     * @return String
     */
    @ResponseBody
    @PostMapping("/logout/{userIdx}")
    public BaseResponse<String> logOut(@PathVariable("userIdx") Integer userIdx) throws BaseException {

        // jwt 토큰 검사
        int userIdxByJwt = jwtService.getUserIdx();
        if(userIdx != userIdxByJwt){
            return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
        }

        try{
            authService.LogOut();
            String result = "로그아웃이 완료되었습니다.";
            return new BaseResponse<>(result);

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
