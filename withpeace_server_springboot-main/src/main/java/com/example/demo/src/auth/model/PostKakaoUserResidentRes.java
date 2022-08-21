package com.example.demo.src.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostKakaoUserResidentRes {
    private Long userIdx;
    private String accessToken;
    private String refreshToken;
}
