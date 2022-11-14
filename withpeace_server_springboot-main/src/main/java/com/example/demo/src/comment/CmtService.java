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

    /** 댓글 생성 **/
    @Transactional
    public PostCommentsRes createCmt(Integer postIdx, PostCommentsReq postCommentsReq, String accessToken) throws BaseException {

        try{
            // 사용자의 userLevle 체크
            String userLevel = postProvider.getUserLevel(postCommentsReq.getUserIdx());

            // Post - Comment
            // userIdx, postIdx, content, isAnonymous
            int commentIdx = cmtDao.insertCmt(postIdx, postCommentsReq);
            System.out.println("추가된 commentIdx : "+commentIdx);

            return new PostCommentsRes(postCommentsReq.getUserIdx(), userLevel, commentIdx, accessToken);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 댓글 수정 **/
    @Transactional
    public PatchCommentsRes patchCmt(Integer commentIdx, PatchCommentsReq patchCommentsReq, String accessToken) throws BaseException {

        // 댓글의 접근권한 확인
        if(cmtProvider.checkCmtUser(commentIdx) != patchCommentsReq.getUserIdx()){
            // 댓글의 작성자가 아닌 경우
            throw new BaseException(PATCH_COMMENTS_INVALID_USER);
        }

        try{
            // 사용자의 userLevle 체크
            String userLevel = postProvider.getUserLevel(patchCommentsReq.getUserIdx());

            // Update - Comment
            // commentIdx, content
            cmtDao.updateCmt(commentIdx, patchCommentsReq);
            return new PatchCommentsRes(patchCommentsReq.getUserIdx(), userLevel, commentIdx, accessToken);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 댓글 삭제 **/
    @Transactional
    public PatchCommentsRes deleteCmt(Integer commentIdx, Long userIdx, String accessToken) throws BaseException {

        // 댓글의 접근권한 확인
        if(cmtProvider.checkCmtUser(commentIdx) != userIdx){
            // 댓글의 작성자가 아닌 경우
            throw new BaseException(PATCH_COMMENTS_INVALID_USER);
        }

        try{
            // 사용자의 userLevel 체크
            String userLevel = postProvider.getUserLevel(userIdx);

            // Delete - Comment
            // commentIdx
            cmtDao.deleteCmt(commentIdx);

            return new PatchCommentsRes(userIdx, userLevel, commentIdx, accessToken);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
