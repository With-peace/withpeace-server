package com.example.demo.src.post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetCommentRes {
    private int commentIdx;
    private int userIdx;
    private String name;
    private String profileImgUrl;
    private String content;
    private String createdAt;
}
