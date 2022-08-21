package com.example.demo.src.post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostPostsReq {
    private Long userIdx;
    private String type;
    private String title;
    private String content;
    private Integer isAnonymous;
}
