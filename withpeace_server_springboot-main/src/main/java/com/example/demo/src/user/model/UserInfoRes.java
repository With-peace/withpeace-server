package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserInfoRes {
    private Long userIdx;
    private String userLevel;
    private String name;
    private int dong;
    private int ho;
    private String profileImg;
    private String accessToken;
}
