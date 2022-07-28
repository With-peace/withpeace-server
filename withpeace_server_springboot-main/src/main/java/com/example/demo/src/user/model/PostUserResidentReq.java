package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostUserResidentReq {
    private String name;
    private String phoneNum;
    private String phoneNumCheck;
    private String email;
    private String password;
    private String inviteCode;
    private Integer dong;
    private Integer ho;
    private String agreeInfo;
}
