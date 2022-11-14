package com.example.demo.src.comment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostCommentsRes {
    private Long userIdx;
    private String userLevel;
    private int commentIdx;
    private String accessToken;
}
