package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserReqList {
    private Long userRequestIdx;
    private String name;
    private int dong;
    private int ho;
    private String phoneNum;
}
