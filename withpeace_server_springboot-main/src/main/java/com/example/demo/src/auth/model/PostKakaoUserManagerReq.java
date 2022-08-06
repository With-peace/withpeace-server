package com.example.demo.src.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostKakaoUserManagerReq {
    private String accessToken;
    private Long userIdx;
    private String name;
    private String phoneNum;
    private String phoneNumCheck;
    private String address;
    private String buildingName;
    private String agreeInfo;
}
