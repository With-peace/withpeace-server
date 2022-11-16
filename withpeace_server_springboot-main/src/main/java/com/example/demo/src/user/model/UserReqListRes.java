package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserReqListRes {
    private Long userIdx;
    private String userLevel;
    private List<UserReqList> UserReqList;
    private String accessToken;
}
