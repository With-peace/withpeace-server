package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserMoveRes {
    private Long userIdx;
    private String userLevel;
    private int buildingIdx;
    private String accessToken;
}
