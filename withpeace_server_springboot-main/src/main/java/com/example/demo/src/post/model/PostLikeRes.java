package com.example.demo.src.post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostLikeRes {
    private Long userIdx;
    private String userLevel;
    private int postLikeIdx;
    private String accessToken;
}
