package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserMoveReq {
    private Long userIdx;
    private String inviteCode;
    private Integer dong;
    private Integer ho;
}
