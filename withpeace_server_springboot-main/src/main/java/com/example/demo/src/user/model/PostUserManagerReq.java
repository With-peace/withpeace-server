package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostUserManagerReq {
    private String name;
    private String phoneNum;
    private String phoneNumCheck;
    private String email;
    private String password;
    private String address;
    private String buildingName;
    private String agreeInfo;
}
