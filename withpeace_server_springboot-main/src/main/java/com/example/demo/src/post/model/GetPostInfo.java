package com.example.demo.src.post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetPostInfo {
    private int postIdx;
    private String title;
    private String content;
    private int likeCount;
    private int commentCount;
    private int imageCount;
    private String updatedAt;
}
