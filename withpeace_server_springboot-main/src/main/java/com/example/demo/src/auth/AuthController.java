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
        for (int i = 0; i < 6; i++) { // ?????? 6?????? ??????
            String ran = Integer.toString(rand.nextInt(10));
            numStr += ran;
        }
        String content = "[WITHPEACE] ???????????? ???????????? "+numStr+" ??? ??????????????????.";

        // ????????? ???????????? ??????
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(120); // ?????? ???????????? ?????? (?????????)
        session.setAttribute("rand", numStr);

        SendSmsResponseDto sendSmsResponseDto = authService.sendSms(phoneNum, content);
        return new BaseResponse<>(sendSmsResponseDto);

    }

    @ResponseBody
    @PostMapping("/phoneAuthOk")
    public BaseResponse<String> phoneAuthOk(HttpServletRequest request, @RequestBody Map<String, String> code) {
        // ?????? ??????????????? ???????????? ??????, ??????????????? body??? ??????
        HttpSession session = request.getSession();
        if(session.getMaxInactiveInterval() > 120){
            // ????????? ??????????????? ??????
            System.out.println("?????? ??????");
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

}
