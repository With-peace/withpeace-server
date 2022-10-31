package com.example.demo.src.post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetPostRes {
    private Long userIdx; // 현재 사용자
    private String userLevel;
    private Long postUserIdx; // 게시글 작성자
    private String profileImgUrl;
    private String name;
    private String title;
    private String content;
    private List<GetPostImageRes> postImageUrls;
    private int likeCount;
    private int commentCount;
    private String createdAt;
    private String likeOrNot;
    private String saveOrNot;
    private List<GetCommentRes> comments;
    private String accessToken;

}
