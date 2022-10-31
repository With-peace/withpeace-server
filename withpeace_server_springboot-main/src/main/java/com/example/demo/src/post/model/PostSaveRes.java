package com.example.demo.src.post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostSaveRes {
    private Long userIdx;
    private String userLevel;
    private int postSaveIdx;
    private String accessToken;
}
