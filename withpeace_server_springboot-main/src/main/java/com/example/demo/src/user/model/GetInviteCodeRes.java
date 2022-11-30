package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetInviteCodeRes {
    private Long userIdx;
    private String userLevel;
    private String inviteCode;
    private String accessToken;
}
