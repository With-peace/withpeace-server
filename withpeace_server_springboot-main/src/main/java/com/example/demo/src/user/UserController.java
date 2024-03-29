package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.post.S3Service;
import com.example.demo.src.user.model.*;
import com.example.demo.src.auth.*;
import com.example.demo.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;

import java.lang.*;
import java.sql.SQLException;
import java.util.List;
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
    @Autowired
    private final S3Service s3Service;


    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService, AuthDao authDao,
                          TokenVerify tokenVerify, S3Service s3Service) {
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
        this.authDao = authDao;
        this.tokenVerify = tokenVerify;
        this.s3Service = s3Service;
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
            return new BaseResponse<>(POST_USERS_RESIDENT_EMPTY_INVITECODE);
        }
        // 호수 입력하지 않았을 때
        if (postUserResidentReq.getHo() == null) {
            return new BaseResponse<>(POST_USERS_RESIDENT_EMPTY_HO);
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
     * 요청 목록 조회 (관리자) API
     * [GET] /users/reqList
     *
     * @return BaseResponse<UserReqListRes>
     */
    @ResponseBody
    @GetMapping("/reqList")
    public BaseResponse<UserReqListRes> getUserReqList(@RequestBody Map<String, Long> userIdx) throws BaseException {

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
            UserReqListRes userReqListRes = userProvider.getUserReqList(userIdx.get("userIdx"), accessToken);
            return new BaseResponse<>(userReqListRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 요청 승인 (관리자) API
     * [PATCH] /users/request/allow/:userRequestIdx
     *
     * @return BaseResponse<UserReqListRes>
     */
    @ResponseBody
    @PatchMapping("/request/allow/{userRequestIdx}")
    public BaseResponse<UserReqAllowRes> userReqAllow(@RequestBody  UserReqAllowReq userReqAllowReq,
                                                      @PathVariable Long userRequestIdx) throws BaseException {

        // 유저인덱스 입력하지 않았을 때
        if (userReqAllowReq.getUserIdx() == null) {
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }

        String first_accessToken = jwtService.getAccessToken();
        // 토큰 검증
        String new_accessToken = tokenVerify.checkToken(userReqAllowReq.getUserIdx());
        String accessToken = null;
        if(first_accessToken != new_accessToken){
            accessToken = new_accessToken;
        }

        // 동 입력하지 않았을 때
        if (userReqAllowReq.getDong() == null) {
            userReqAllowReq.setDong(101);
        }

        // 호수 입력하지 않았을 때
        if (userReqAllowReq.getHo() == null) {
            return new BaseResponse<>(POST_USERS_RESIDENT_EMPTY_HO);
        }

        try {
            UserReqAllowRes userReqAllowRes = userService.userReqAllow(userReqAllowReq, userRequestIdx, accessToken);
            return new BaseResponse<>(userReqAllowRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 요청 거절 (관리자) API
     * [PATCH] /users/request/refuse/:userRequestIdx
     *
     * @return BaseResponse<UserReqListRes>
     */
    @ResponseBody
    @PatchMapping("/request/refuse/{userRequestIdx}")
    public BaseResponse<UserReqAllowRes> userReqRefuse(@RequestBody Map<String, Long> userIdx,
                                                      @PathVariable Long userRequestIdx) throws BaseException {

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
            UserReqAllowRes userReqAllowRes = userService.userReqRefuse(userIdx.get("userIdx"), userRequestIdx, accessToken);
            return new BaseResponse<>(userReqAllowRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 초대코드 조회 (관리자) API
     * [GET] /users/inviteCode
     *
     * @return BaseResponse<GetInviteCodeRes>
     */
    @ResponseBody
    @GetMapping("/inviteCode")
    public BaseResponse<GetInviteCodeRes> getInviteCode(@RequestBody Map<String, Long> userIdx) throws BaseException {

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
            GetInviteCodeRes getInviteCodeRes = userProvider.getInviteCode(userIdx.get("userIdx"), accessToken);
            return new BaseResponse<>(getInviteCodeRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 이사 (회원 주소 이동) API
     * [PATCH] /users/move
     *
     * @return BaseResponse<UserMoveRes>
     */
    @ResponseBody
    @PatchMapping("/move")
    public BaseResponse<UserMoveRes> userMove(@RequestBody UserMoveReq userMoveReq) throws BaseException {

        // 유저인덱스 입력하지 않았을 때
        if (userMoveReq.getUserIdx() == null) {
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }

        String first_accessToken = jwtService.getAccessToken();
        // 토큰 검증
        String new_accessToken = tokenVerify.checkToken(userMoveReq.getUserIdx());
        String accessToken = null;
        if(first_accessToken != new_accessToken){
            accessToken = new_accessToken;
        }

        // 초대코드 입력하지 않았을 때
        if (userMoveReq.getInviteCode() == null) {
            return new BaseResponse<>(POST_USERS_RESIDENT_EMPTY_INVITECODE);
        }

        // 동 입력하지 않았을 때
        if (userMoveReq.getDong() == null) {
            userMoveReq.setDong(101);
        }

        // 호수 입력하지 않았을 때
        if (userMoveReq.getHo() == null) {
            return new BaseResponse<>(POST_USERS_RESIDENT_EMPTY_HO);
        }

        try {
            UserMoveRes userMoveRes = userService.userMove(userMoveReq, accessToken);
            return new BaseResponse<>(userMoveRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 회원탈퇴 API
     * [PATCH] /users/withdrawal
     *
     * @return BaseResponse<UserWithdrawalRes>
     */
    @ResponseBody
    @PatchMapping("/withdrawal")
    public BaseResponse<UserWithdrawalRes> UserWithdrawal(@RequestBody UserWithdrawalReq userWithdrawalReq) throws BaseException {

        // 유저인덱스 입력하지 않았을 때
        if (userWithdrawalReq.getUserIdx() == null) {
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }

        String first_accessToken = jwtService.getAccessToken();
        // 토큰 검증
        String new_accessToken = tokenVerify.checkToken(userWithdrawalReq.getUserIdx());
        String accessToken = null;
        if(first_accessToken != new_accessToken){
            accessToken = new_accessToken;
        }

        // 탈퇴사유 입력하지 않았을 때
        if (userWithdrawalReq.getReason() == null) {
            userWithdrawalReq.setReason(null);
        }

        // 비밀번호 입력하지 않았을 때
        if (userWithdrawalReq.getPassword() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }

        try {
            UserWithdrawalRes userWithdrawalRes = userService.UserWithdrawal(userWithdrawalReq);
            return new BaseResponse<>(userWithdrawalRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 프로필 사진 수정 API
     * [PATCH] /users/profileImage
     *
     * @return BaseResponse<UserWithdrawalRes>
     */
    @ResponseBody
    @PatchMapping("/profileImage")
    public BaseResponse<UserProfileImgRes> PatchProfileImg(@RequestPart("content") Map<String, Long> userIdx,
                                                           @RequestPart("imgUrl") MultipartFile profileImgUrl) throws BaseException {

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

        // 프로필이미지 입력하지 않았을 때
        if (profileImgUrl == null) {
            return new BaseResponse<>(PATCH_USERS_IMGURL_EMPTY);
        }

        String imgPath = s3Service.profileImgUpload(profileImgUrl);

        try {
            // db에서 사용자의 profileImg 가져옴
            String userProfileImg = userProvider.userProfileImg(userIdx.get("userIdx"));
            if(userProfileImg != "https://withpeace-post.s3.ap-northeast-2.amazonaws.com/profileImg/default_profileImg.png"){
                // 가져온 profileImg가 기본 이미지 경로가 아닐 경우

                // 이미지 파일 이름 추출
                int index = userProfileImg.lastIndexOf("/");
                // S3에 이미지 파일 삭제
                s3Service.deleteFile(userProfileImg.substring(index+1));
            }

            // s3에 새로 저장된 프로필 이미지의 경로를 DB에 저장
            UserProfileImgRes userProfileImgRes = userService.PatchProfileImg(userIdx.get("userIdx"), imgPath, accessToken);
            return new BaseResponse<>(userProfileImgRes);
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
