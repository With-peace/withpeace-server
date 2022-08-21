package com.example.demo.src.auth;

import com.example.demo.config.BaseException;
import com.example.demo.src.auth.model.*;
import com.example.demo.src.user.*;
import com.example.demo.utils.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Base64;

import java.util.*;
import java.lang.*;

import com.example.demo.src.auth.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import static com.example.demo.config.BaseResponseStatus.*;

// kakao
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


@Getter
@Setter
//@AllArgsConstructor
@Component
@Service
public class AuthService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserProvider userProvider;
    private final UserDao userDao;
    private final AuthDao authDao;
    private final AuthProvider authProvider;
    private final JwtService jwtService;
    private final ApplicationNaverSENS applicationNaverSENS;


    @Autowired
    public AuthService(UserProvider userProvider, UserDao userDao, AuthDao authDao, AuthProvider authProvider, JwtService jwtService, ApplicationNaverSENS applicationNaverSENS) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.authDao = authDao;
        this.authProvider = authProvider;
        this.jwtService = jwtService;
        this.applicationNaverSENS = applicationNaverSENS;

    }

    /** 인증번호 발송 **/
    public SendSmsResponseDto sendSms(String phoneNum, String content) throws ParseException, JsonProcessingException, UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, URISyntaxException {
        String timestamp = Long.toString(System.currentTimeMillis());
        List<MessagesRequestDto> messages = new ArrayList<>();
        // 보내는 사람에게 내용을 보냄
        messages.add(new MessagesRequestDto(phoneNum,content)); // content부분이 내용임

        // 전체 json에 대해 메시지를 만든다
        SmsRequestDto smsRequestDto = new SmsRequestDto("SMS", "COMM", "82", applicationNaverSENS.getSendFrom(), "withpeace", messages);

        // 쌓아온 바디를 json 형태로 변환시켜준다
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(smsRequestDto);

        // 헤더에서 여러 설정값들을 잡아준다.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", timestamp);
        headers.set("x-ncp-iam-access-key", applicationNaverSENS.getAccessKey());

        // signature 서명하기
        String sig = makeSignature(timestamp);
        System.out.println("sig -> " + sig);
        headers.set("x-ncp-apigw-signature-v2", sig);

        // 위에서 조립한 jsonBody와 헤더를 조립한다
        HttpEntity<String> body = new HttpEntity<>(jsonBody, headers);
        System.out.println(body.getBody());

        // restTemplate로 post 요청을 보낸다. 별 일 없으면 202 코드 반환된다
        RestTemplate restTemplate = new RestTemplate();
        SendSmsResponseDto sendSmsResponseDto = restTemplate.postForObject(new URI("https://sens.apigw.ntruss.com/sms/v2/services/"+applicationNaverSENS.getSeviceId()+"/messages"), body, SendSmsResponseDto.class);
        System.out.println(sendSmsResponseDto.getStatusCode());
        return sendSmsResponseDto;
    }

    public String makeSignature(String time) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException {
        String space = " ";	// one space
        String newLine = "\n"; // new line
        String method = "POST"; // method
        String url = "/sms/v2/services/"+applicationNaverSENS.getSeviceId()+"/messages";	// url (include query string)
        String timestamp = time;			// current timestamp (epoch)
        String accessKey = applicationNaverSENS.getAccessKey();			// access key id (from portal or Sub Account)
        String secretKey = applicationNaverSENS.getSecretKey();

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();


        SecretKeySpec signingKey;
        String encodeBase64String;
        try {

            signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
            encodeBase64String = Base64.getEncoder().encodeToString(rawHmac);
        } catch (UnsupportedEncodingException e) {
            encodeBase64String = e.toString();
        }

        return encodeBase64String;
    }

    /** 토큰 받기 - kakao **/
    public String getKakaoAccessToken (String code) {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=8607b8d717c64553b661fd399330ab4e"); // TODO REST_API_KEY 입력
            sb.append("&redirect_uri=http://localhost:9000/auth/kakao"); // TODO 인가코드 받은 redirect_uri 입력
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token : " + access_Token);
            System.out.println("refresh_token : " + refresh_Token);

            br.close();
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return access_Token;
    }


    /** 사용자 정보 가져오기 - kakao **/
    public KakaoUserInfo getKakaoUserInfo(String token) {

        String reqURL = "https://kapi.kakao.com/v2/user/me";

        //access_token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token); //전송할 header 작성, access_token전송
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                System.out.println(line);
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리로 JSON파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            long id = element.getAsJsonObject().get("id").getAsLong();
            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
            String email = "";
            if(hasEmail){
                email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            }
//            byte[] bytes = element.getAsJsonObject().get("properties").getAsJsonObject().get("nickname").getAsString().getBytes(StandardCharsets.UTF_8);
//            String nickname = new String(bytes, "UTF-8");
            String nickname = element.getAsJsonObject().get("properties").getAsJsonObject().get("nickname").getAsString();

            System.out.println("id : " + id);
            System.out.println("email : " + email);
            System.out.println("nickname : " + nickname);

            br.close();

           return new KakaoUserInfo(id, email, nickname);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 카카오 관리자 회원가입 **/
    @Transactional
    public PostKakaoUserManagerRes createManagerReq(PostKakaoUserManagerReq postKakaoUserManagerReq) throws BaseException {

        // 초대코드 생성
        int length = 15;
        boolean useLetters = true;
        boolean useNumbers = true;
        String inviteCode = RandomStringUtils.random(length, useLetters, useNumbers);
        // 초대코드 중복 확인
        while(userProvider.checkInviteCode(inviteCode) == 1){
            inviteCode = RandomStringUtils.random(length, useLetters, useNumbers);
        }
        System.out.println(inviteCode); //

        try{
            // Post - Building
            // name, address, inviteCode
            int buildingIdx = authDao.postBuilding(postKakaoUserManagerReq, inviteCode);
            System.out.println("buildingIdx : "+buildingIdx);

            // Post - User
            // name, phoneNum, email, password, signupType
            Long userIdx = authDao.postUserManager(postKakaoUserManagerReq, buildingIdx);
            System.out.println("userIdx : "+userIdx);

            String accessToken = jwtService.createAccessToken(userIdx);
            String refreshToken = jwtService.createRefreshToken(userIdx);

            // Update - User
            // refreshToken
            userDao.SaveRefeshTokenUserManager(userIdx, refreshToken);

            // 추가된 유저요청인덱스, 건물 인덱스, accessToken, refreshToken 반환
            return new PostKakaoUserManagerRes(userIdx, buildingIdx, accessToken, refreshToken);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /** 카카오 주민 회원가입 **/
    @Transactional
    public PostKakaoUserResidentRes createResidentReq(PostKakaoUserResidentReq postKakaoUserResidentReq) throws BaseException {

        // 초대코드 존재확인
        if(userDao.isExistInviteCode(postKakaoUserResidentReq.getInviteCode()) == 0){
            throw new BaseException(INVALID_INVITECODE);
        }

        try{
            // Get - Building
            // buildingIdx
            int buildingIdx = userProvider.getBuildingIdx(postKakaoUserResidentReq.getInviteCode());

            // Post - User
            // userIdx, buildinIdx, name, phoneNum, dong, ho
            Long userIdx = authDao.postUserResident(postKakaoUserResidentReq, buildingIdx);
            System.out.println("userIdx : "+userIdx);

            String accessToken = jwtService.createAccessToken(userIdx);
            String refreshToken = jwtService.createRefreshToken(userIdx);

            // Update - User
            // refreshToken
            userDao.SaveRefeshTokenUserManager(userIdx, refreshToken);

            // 추가된 유저인덱스, jwt토큰 반환
            return new PostKakaoUserResidentRes(userIdx, accessToken, refreshToken);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /** 카카오 로그인 **/
    public PostLoginRes KakaoLogIn(Long userIdx) throws BaseException {

        try{
            String accessToken = jwtService.createAccessToken(userIdx);
            String refreshToken = jwtService.createRefreshToken(userIdx);

            // refreshToken을 DB에 저장 (갱신)
            // Update - User
            // refreshToken
            userDao.SaveRefeshTokenUserManager(userIdx, refreshToken);

            return new PostLoginRes(userIdx, accessToken, refreshToken);
        }
        catch (Exception exception){
            throw new BaseException(FAILED_TO_KAKAOLOGIN);
        }
    }


    /** 일반 로그인 **/
    public PostLoginRes LogIn(PostLoginReq postLoginReq) throws BaseException {
        UserInfo userInfo = authDao.getUserInfo(postLoginReq);
        String encryptPwd;

        try{
            // 암호화 -> SHA256
            encryptPwd = new SHA256().encrypt(postLoginReq.getPassword()); // 암호화
        }
        catch (Exception exception){
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        // 암호화 준 비밀번호를 확인
        if(userInfo.getPassword().equals(encryptPwd)){
            // 비교를 해주고, 이상이 없다면 jwt 발급
            Long userIdx = userInfo.getUserIdx();

            String accessToken = jwtService.createAccessToken(userIdx);
            String refreshToken = jwtService.createRefreshToken(userIdx);

            // refreshToken을 DB에 저장 (갱신)
            // Update - User
            // refreshToken
            userDao.SaveRefeshTokenUserManager(userIdx, refreshToken);

            return new PostLoginRes(userIdx, accessToken, refreshToken);
        }
        else
            throw new BaseException(FAILED_TO_LOGIN);
    }


    /** 카카오 토큰 갱신 **/
    public String kakaoupdateToken(Long userIdx, String refreshToken) {

        String reqURL = "https://kauth.kakao.com/oauth/token";

        //access_token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=refresh_token");
            sb.append("&client_id=8607b8d717c64553b661fd399330ab4e");
            sb.append("&redirect_uri="+refreshToken);
            bw.write(sb.toString());
            bw.flush();


            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리로 JSON파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            String access_token = element.getAsJsonObject().get("access_token").getAsString();
            String token_type = element.getAsJsonObject().get("token_type").getAsString();
            Integer expires_in = element.getAsJsonObject().get("expires_in").getAsInt(); // 초

            System.out.println("access_token : " + access_token);
            System.out.println("token_type : " + token_type);
            System.out.println("expires_in : " + expires_in);

            br.close();

            // DB - refreshToken 갱신
            authDao.saveRefreshToken(userIdx, refreshToken);

            return access_token;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /** 로그아웃 **/
    public void logOut(Long userIdx) throws BaseException {
        try {
            authDao.logOut(userIdx);
        } catch (Exception exception) {
            throw new BaseException(LOGOUT_ERROR);
        }
    }

}