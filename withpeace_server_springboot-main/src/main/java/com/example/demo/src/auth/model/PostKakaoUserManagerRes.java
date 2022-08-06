package com.example.demo.src.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostKakaoUserManagerRes {
    private Long userIdx;
    private int buildingIdx;
    private String jwt;
}
