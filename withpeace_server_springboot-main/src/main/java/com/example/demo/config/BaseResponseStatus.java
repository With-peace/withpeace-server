package com.example.demo.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),
    INVALID_USER_ACCESSTOKEN(false,2004,"권한이 없는 유저의 접근입니다.(ACCESS TOKEN)"),
    INVALID_USER_REFRESHTOKEN(false,2005,"권한이 없는 유저의 접근입니다.(REFRESH TOKEN)"),
    ISEXPIRED_REFRESH_TOKEN(false,2006,"토큰이 만료되어 로그아웃 되었습니다.(REFRESH TOKEN)"),
    NOTEQUALS_DB_HEADER_REFRESH_TOKEN(false,2007,"토큰이 유효하지 않습니다.(REFRESH TOKEN)"),
    INVALID_ACCESSTOKEN(false,2008,"유효하지 않은 JWT입니다.(ACCESS TOKEN)"),

    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),

    // [POST] /users/signup
    POST_USERS_INVALID_PHONENUM(false, 2014, "휴대폰번호 형식을 확인해주세요."),
    POST_USERS_EMPTY_NAME(false, 2015, "이름을 입력해주세요."),
    POST_USERS_EMPTY_PHONENUM(false, 2016, "휴대폰 번호를 입력해주세요."),
    POST_USERS_CHECK_PHONENUM(false, 2017, "휴대폰 인증을 완료해주세요."),
    POST_USERS_EMPTY_EMAIL(false, 2018, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2019, "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,2020,"중복된 이메일입니다."),
    POST_USERS_EMPTY_PASSWORD(false, 2021, "비밀번호를 입력해주세요."),
    POST_USERS_INVALID_PASSWORD(false, 2022, "비밀번호 형식을 확인해주세요."),
    POST_USERS_EMPTY_AGREEINFO(false, 2014, "개인정보 수집 및 이용에 동의해주세요."),

    // [POST] /users/signup/manager
    POST_USERS_MANAGER_EMPTY_ADDRESS(false, 2023, "주소를 입력해주세요."),
    POST_USERS_MANAGER_EMPTY_BUILDINGNAME(false, 2024, "건물 이름 입력해주세요."),

    // [POST] /users/signup/resident
    POST_USERS_RESIDENT_EMPTY_INVITECODE(false, 2025, "초대코드를 입력해주세요."),
    POST_USERS_RESIDENT_EMPTY_DONG(false, 2026, "동을 입력해주세요."),
    POST_USERS_RESIDENT_EMPTY_HO(false, 2027, "호수를 입력해주세요."),

    // [POST] /auth/phoneAuthOk
    POST_AUTH_SESSION_TIMEOUT(false, 2040, "인증번호가 만료되었습니다."),

    //[POST] /auth/logout/{userIdx}
    LOGOUT_ERROR(false, 2050, "로그아웃에 실패하였습니다."),

    // [POST] /posts
    POST_POSTS_EMPTY_TITLE(false, 2060, "제목을 입력해주세요."),
    POST_POSTS_EMPTY_CONTENT(false, 2061, "내용을 입력해주세요."),
    POST_POSTS_EMPTY_ISANONYMOUS(false, 2062, "익명여부를 확인해주세요."),
    POST_POSTS_EMPTY_TYPE(false, 2063, "타입을 입력해주세요."),
    POST_POSTS_INVAILD_TYPE(false, 2064, "타입을 확인해주세요."),

    // [PATCH] /posts/delete/:postIdx
    POST_DELETE_EMPTY_POSTIDX(false, 2070, "게시글 인덱스를 확인해주세요."),
    POST_DELETE_INVALID_POSTIDX(false,2071,"존재하지 않는 게시글입니다."),
    POST_DELETE_INVALID_USER(false,2072,"접근권한이 없는 유저입니다."),

    // [POST] /comments/:postIdx
    POST_COMMENTS_EMPTY_CONTENT(false, 2080, "내용을 입력해주세요."),
    POST_COMMENTS_EMPTY_ISANONYMOUS(false, 2081, "익명여부를 확인해주세요."),

    // [PATCH] /comments/:commentIdx
    PATCH_COMMENTS_EMPTY_CMTIDX(false, 2082, "댓글 인덱스를 입력해주세요."),
    PATCH_COMMENTS_INVALID_USER(false,2083,"접근권한이 없는 유저입니다."),

    // [PATCH] /users/profileImage
    PATCH_USERS_IMGURL_EMPTY(false, 2085, "프로필 이미지를 입력해주세요."),


    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,3014,"없는 아이디거나 비밀번호가 틀렸습니다."),
    INVALID_INVITECODE(false,3015,"존재하지 않는 초대코드 입니다."),

    FAILED_TO_KAKAOLOGIN(false,3016,"[카카오 로그인] 없는 아이디거나 비밀번호가 틀렸습니다."),

    NOT_EQUAL_PASSWORD(false,3017,"비밀번호가 일치하지 않습니다."),



    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,4014,"유저네임 수정 실패"),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다."),

    // [PATCH] /posts/delete/:postIdx
    POST_DELETE_POSTIMAGE(false, 4015, "이미지 파일 삭제에 실패하였습니다.");

    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
