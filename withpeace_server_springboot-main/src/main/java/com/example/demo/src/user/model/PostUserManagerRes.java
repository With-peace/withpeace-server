package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostUserManagerRes {
    private Long userIdx;
    private String userLevel;
    private int buildingIdx;
    private String accessToken;
    private String refreshToken;
}
