package com.example.demo.src.post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetNoticeListRes {
    private List<GetNoticeList> noticeList;
    private String accessToken;
}
