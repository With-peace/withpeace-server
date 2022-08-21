package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.math.BigInteger;
import java.lang.Long;

@Getter
@Setter
@AllArgsConstructor
public class PostUserResidentRes {
    private Long userIdx;
    private String accessToken;
    private String refreshToken;
}
