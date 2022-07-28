package com.example.demo.src.auth;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.auth.model.*;
import com.example.demo.src.auth.model.SendSmsResponseDto;
import com.example.demo.src.user.model.PostUserManagerRes;
import com.example.demo.utils.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.*;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

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

}
