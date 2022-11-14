package com.example.demo.src.comment;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.comment.model.*;
import com.example.demo.src.post.model.PostPostsReq;
import com.example.demo.src.post.model.PostPostsRes;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.TokenVerify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;

@RestController
@RequestMapping("/comments")
public class CmtController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final CmtProvider cmtProvider;
    @Autowired
    private final CmtService cmtService;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final TokenVerify tokenVerify;


    public CmtController(CmtProvider cmtProvider, CmtService cmtService, JwtService jwtService, TokenVerify tokenVerify) {
        this.cmtProvider = cmtProvider;
        this.cmtService = cmtService;
        this.jwtService = jwtService;
        this.tokenVerify = tokenVerify;
    }

    /**
     * 댓글 생성 API
     * [POST] /comments/:postIdx
     *
     * @return BaseResponse<PostCommentsRes>
     */
    @ResponseBody
    @PostMapping("/{postIdx}")
    public BaseResponse<PostCommentsRes> createCmt(@PathVariable("postIdx") Integer postIdx,
                                                    @RequestBody PostCommentsReq postCommentsReq) throws BaseException {
        // 유저인덱스 입력하지 않았을 때
        if (postCommentsReq.getUserIdx() == null) {
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }

        String first_accessToken = jwtService.getAccessToken();
        // 토큰 검증
        String new_accessToken = tokenVerify.checkToken(postCommentsReq.getUserIdx());
        String accessToken = null;
        if(first_accessToken != new_accessToken){
            accessToken = new_accessToken;
        }

        // 게시글 인덱스를 입력하지 않았을 떄
        if (postIdx == null) {
            return new BaseResponse<>(POST_DELETE_EMPTY_POSTIDX);
        }
        // 내용을 입력하지 않았을 때
        if (postCommentsReq.getContent() == null) {
            return new BaseResponse<>(POST_COMMENTS_EMPTY_CONTENT);
        }
        // 익명여부를 입력하지 않았을 때
        if (postCommentsReq.getIsAnonymous() == null) {
            return new BaseResponse<>(POST_COMMENTS_EMPTY_ISANONYMOUS);
        }

        try {
            PostCommentsRes postCommentsRes = cmtService.createCmt(postIdx, postCommentsReq, accessToken);
            return new BaseResponse<>(postCommentsRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 댓글 수정 API
     * [PATCH] /comments/patch/:commentIdx
     *
     * @return BaseResponse<PostCommentsRes>
     */
    @ResponseBody
    @PatchMapping("/patch/{commentIdx}")
    public BaseResponse<PatchCommentsRes> createCmt(@PathVariable("commentIdx") Integer commentIdx,
                                                    @RequestBody PatchCommentsReq patchCommentsReq) throws BaseException {
        // 유저인덱스 입력하지 않았을 때
        if (patchCommentsReq.getUserIdx() == null) {
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }

        String first_accessToken = jwtService.getAccessToken();
        // 토큰 검증
        String new_accessToken = tokenVerify.checkToken(patchCommentsReq.getUserIdx());
        String accessToken = null;
        if(first_accessToken != new_accessToken){
            accessToken = new_accessToken;
        }

        // 댓글 인덱스를 입력하지 않았을 떄
        if (commentIdx == null) {
            return new BaseResponse<>(PATCH_COMMENTS_EMPTY_CMTIDX);
        }
        // 내용을 입력하지 않았을 때
        if (patchCommentsReq.getContent() == null) {
            return new BaseResponse<>(POST_COMMENTS_EMPTY_CONTENT);
        }

        try {
            PatchCommentsRes patchCommentsRes = cmtService.patchCmt(commentIdx, patchCommentsReq, accessToken);
            return new BaseResponse<>(patchCommentsRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 댓글 삭제 API
     * [DELETE] /comments/delete/:commentIdx
     *
     * @return BaseResponse<PatchCommentsRes>
     */
    @ResponseBody
    @DeleteMapping("/delete/{commentIdx}")
    public BaseResponse<PatchCommentsRes> createCmt(@PathVariable("commentIdx") Integer commentIdx,
                                                    @RequestBody Map<String, Long> userIdx) throws BaseException {
        // 유저인덱스 입력하지 않았을 때
        if (userIdx.get("userIdx") == null) {
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }

        String first_accessToken = jwtService.getAccessToken();
        // 토큰 검증
        String new_accessToken = tokenVerify.checkToken(userIdx.get("userIdx"));
        String accessToken = null;
        if(first_accessToken != new_accessToken){
            accessToken = new_accessToken;
        }

        // 댓글 인덱스를 입력하지 않았을 떄
        if (commentIdx == null) {
            return new BaseResponse<>(PATCH_COMMENTS_EMPTY_CMTIDX);
        }

        try {
            PatchCommentsRes patchCommentsRes = cmtService.deleteCmt(commentIdx, userIdx.get("userIdx"), accessToken);
            return new BaseResponse<>(patchCommentsRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
