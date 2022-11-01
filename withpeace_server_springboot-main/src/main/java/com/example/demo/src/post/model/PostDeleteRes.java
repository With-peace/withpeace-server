package com.example.demo.src.post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostDeleteRes {
    private Long userIdx;
    private String userLevel;
    private int postIdx;
    private String accessToken;
}
