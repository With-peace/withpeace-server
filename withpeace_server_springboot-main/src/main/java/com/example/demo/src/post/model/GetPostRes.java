package com.example.demo.src.post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetPostRes {
    private int userIdx;
    private String profileImgUrl;
    private String name;
    private String title;
    private String content;
    private List<GetPostImageRes> postImageUrls;
    private int likeCount;
    private int commentCount;
    private String updatedAt;
    private String likeOrNot;
    private String saveOrNot;
    private List<GetCommentRes> comments;
    private String accessToken;

}
