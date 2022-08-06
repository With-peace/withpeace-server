package com.example.demo.src.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostKakaoUserResidentReq {
    private String accessToken;
    private Long userIdx;
    private String name;
    private String phoneNum;
    private String inviteCode;
    private Integer dong;
    private Integer ho;
    private String agreeInfo;
}
