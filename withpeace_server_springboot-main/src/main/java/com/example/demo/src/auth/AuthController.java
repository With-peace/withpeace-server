package com.example.demo.src.auth;

import com.example.demo.config.BaseResponse;
import com.example.demo.src.auth.model.*;
import com.example.demo.src.auth.model.SendSmsResponseDto;
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
import java.util.Random;

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
    public BaseResponse<SendSmsResponseDto> phoneAuth(@RequestPaㅔㅗram String phoneNum) throws UnsupportedEncodingException, ParseException, NoSuchAlgorithmException, URISyntaxException, InvalidKeyException, JsonProcessingException {

        Random rand = new Random();
        String numStr = "";
        for (int i = 0; i < 6; i++) { // 랜덤 6자리 숫자
            String ran = Integer.toString(rand.nextInt(10));
            numStr += ran;
        }
        String content = "[WITHPEACE] 회원가입 인증번호 "+numStr+" 를 입력해주세요.";

        SendSmsResponseDto sendSmsResponseDto = authService.sendSms(phoneNum, content);
        return new BaseResponse<>(sendSmsResponseDto);

    }

//    @PostMapping("phoneAuthOk")
//    @ResponseBody
//    public Boolean phoneAuthOk() {
//        String rand = (String) session.getAttribute("rand");
//        String code = (String) request.getParameter("code");
//
//        System.out.println(rand + " : " + code);
//
//        if (rand.equals(code)) {
//            session.removeAttribute("rand");
//            return false;
//        }
//
//        return true;
//    }

}
