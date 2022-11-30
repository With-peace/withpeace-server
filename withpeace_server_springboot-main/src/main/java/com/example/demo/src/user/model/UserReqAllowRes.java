package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserReqAllowRes {
    private Long userIdx;
    private String userLevel;
    private Long userRequestIdx;
    private String accessToken;
}
