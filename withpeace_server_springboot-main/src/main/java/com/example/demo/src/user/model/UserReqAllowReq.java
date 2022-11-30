package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserReqAllowReq {
    private Long userIdx;
    private Integer dong;
    private Integer ho;
}
