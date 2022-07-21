package com.example.demo.src.auth.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.security.Timestamp;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SendSmsResponseDto {
    private String requestId;
    private String requestTime;
    private String statusCode;
    private String statusName;
}