package com.example.demo.src.comment;


import com.example.demo.config.BaseException;
import com.example.demo.src.auth.AuthDao;
import com.example.demo.src.comment.model.*;
import com.example.demo.src.post.PostProvider;
import com.example.demo.src.post.model.PostPostsReq;
import com.example.demo.src.post.model.PostPostsRes;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class CmtService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CmtDao cmtDao;
    private final CmtProvider cmtProvider;
    private final PostProvider postProvider;
    private final JwtService jwtService;
    private DataSource dataSource;


    @Autowired
    public CmtService(CmtDao cmtDao, CmtProvider cmtProvider, PostProvider postProvider, JwtService jwtService) {
        this.cmtDao = cmtDao;
        this.cmtProvider = cmtProvider;
        this.postProvider = postProvider;
        this.jwtService = jwtService;

    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /** 게시글 생성 **/
    @Transactional
    public PostCommentsRes createCmt(Integer postIdx, PostCommentsReq postCommentsReq, String accessToken) throws BaseException {

        try{
            // 사용자의 userLevle 체크
            String userLevel = postProvider.getUserLevel(postCommentsReq.getUserIdx());

            // Post - Comment
            // userIdx, postIdx, content, isAnonymous
            int commentIdx = cmtDao.insertCmt(postIdx, postCommentsReq);
            System.out.println("추가된 commentIdx : "+commentIdx);

            // 추가된 게시글인덱스
            return new PostCommentsRes(postCommentsReq.getUserIdx(), userLevel, commentIdx, accessToken);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
