package com.example.demo.src.post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetPostList {
    private Long userIdx;
    private String userLevel;
    private List<GetPostInfo> postList;
    private String accessToken;
}
