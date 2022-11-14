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

}
