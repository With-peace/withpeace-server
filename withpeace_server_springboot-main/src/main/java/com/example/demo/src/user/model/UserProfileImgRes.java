package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserProfileImgRes {
    private Long userIdx;
    private String userLevel;
    private String profileImg;
    private String accessToken;
}
