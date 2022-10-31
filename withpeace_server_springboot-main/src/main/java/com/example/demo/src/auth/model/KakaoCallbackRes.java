package com.example.demo.src.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
public class KakaoCallbackRes {
    private Long userIdx;
    private String userLevel;
    private String name;
    private String accessToken;
    private String refreshToken;
    private String nextLevel;
}
