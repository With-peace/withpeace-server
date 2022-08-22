package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.post.model.*;
import com.example.demo.src.auth.*;
import com.example.demo.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.example.demo.config.BaseResponseStatus.*;

import java.lang.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
public class PostController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PostProvider postProvider;
    private final PostService postService;
    private final JwtService jwtService;
    private final AuthDao authDao;
    private final TokenVerify tokenVerify;

    @Autowired
    public PostController(PostProvider postProvider, PostService postService, JwtService jwtService, AuthDao authDao, TokenVerify tokenVerify) {
        this.postProvider = postProvider;
        this.postService = postService;
        this.jwtService = jwtService;
        this.authDao = authDao;
        this.tokenVerify = tokenVerify;
    }

    /**
     * 게시글 생성 API
     * [POST] /posts
     *
     * @return BaseResponse<PostPostsRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostPostsRes> createPost(@RequestPart PostPostsReq postPostsReq, List<MultipartFile> postImage
                                                 ) throws BaseException {
        // 유저인덱스 입력하지 않았을 때
        if (postPostsReq.getUserIdx() == null) {
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }

        String first_accessToken = jwtService.getAccessToken();
        // 토큰 검증
        String new_accessToken = tokenVerify.checkToken(postPostsReq.getUserIdx());
        String accessToken = null;
        if(first_accessToken != new_accessToken){
            accessToken = new_accessToken;
        }

        String type = postPostsReq.getType();

        // 타입을 입력하지 않았을 떄
        if (type == null) {
            return new BaseResponse<>(POST_POSTS_EMPTY_TYPE);
        }
        // 유효한 게시글 타입이 아닐 때
        if (!type.equals("notice")&&!type.equals("general")&&!type.equals("information")&&!type.equals("share")&&!type.equals("group")&&!type.equals("secondhand")) {
            return new BaseResponse<>(POST_POSTS_INVAILD_TYPE);
        }
        // 제목을 입력하지 않았을 때
        if (postPostsReq.getTitle() == null) {
            return new BaseResponse<>(POST_POSTS_EMPTY_TITLE);
        }
        // 내용을 입력하지 않았을 때
        if (postPostsReq.getContent() == null) {
            return new BaseResponse<>(POST_POSTS_EMPTY_CONTENT);
        }
        // 익명여부를 입력하지 않았을 때
        if (postPostsReq.getIsAnonymous() == null) {
            return new BaseResponse<>(POST_POSTS_EMPTY_ISANONYMOUS);
        }

        try {

            PostPostsRes postPostsRes = postService.createPost(postImage, postPostsReq, accessToken);
            return new BaseResponse<>(postPostsRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 게시글 삭제 API
     * [PATCH] /posts/delete/:postIdx
     *
     * @return BaseResponse<PostDeleteRes>
     */
    @ResponseBody
    @PatchMapping("/delete/{postIdx}")
    public BaseResponse<PostDeleteRes> deletePost(@PathVariable("postIdx") Integer postIdx, @RequestBody Map<String, Long> userIdx) throws BaseException {

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

        // 게시글 인덱스를 입력하지 않았을 떄
        if (postIdx == null) {
            return new BaseResponse<>(POST_DELETE_EMPTY_POSTIDX);
        }

        try {

            PostDeleteRes postDeleteRes = postService.deletePost(postIdx, userIdx.get("userIdx"), accessToken);
            return new BaseResponse<>(postDeleteRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 게시글 좋아요 API
     * [POST] /posts/like/:postIdx
     * @return BaseResponse<PostLikeRes>
     */
    @ResponseBody
    @PostMapping("/like/{postIdx}")
    public BaseResponse<PostLikeRes> createPostLike(@PathVariable("postIdx") Integer postIdx, @RequestBody Map<String, Long> userIdx) throws BaseException {

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

        // 게시글 인덱스를 입력하지 않았을 떄
        if (postIdx == null) {
            return new BaseResponse<>(POST_DELETE_EMPTY_POSTIDX);
        }

        try {
            PostLikeRes postLikeRes = postService.createPostLike(userIdx.get("userIdx"), postIdx, accessToken);
            return new BaseResponse<>(postLikeRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 게시글 좋아요 취소 API
     * [POST] /posts/deleteLike/:postLikeIdx
     * @return BaseResponse<PostLikeRes>
     */
    @ResponseBody
    @DeleteMapping("/deleteLike/{postLikeIdx}")
    public BaseResponse<PostLikeRes> deletePostLike(@PathVariable("postLikeIdx") Integer postLikeIdx, @RequestBody Map<String, Long> userIdx) throws BaseException {

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

        // 게시글 인덱스를 입력하지 않았을 떄
        if (postLikeIdx == null) {
            return new BaseResponse<>(POST_DELETE_EMPTY_POSTLIKEIDX);
        }

        try {
            PostLikeRes postLikeRes = postService.deletePostLike(userIdx.get("userIdx"), postLikeIdx, accessToken);
            return new BaseResponse<>(postLikeRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
