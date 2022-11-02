package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
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
    private final S3Service s3Service;

    @Autowired
    public PostController(PostProvider postProvider, PostService postService, JwtService jwtService, AuthDao authDao, TokenVerify tokenVerify, S3Service s3Service) {
        this.postProvider = postProvider;
        this.postService = postService;
        this.jwtService = jwtService;
        this.authDao = authDao;
        this.tokenVerify = tokenVerify;
        this.s3Service = s3Service;
    }

    /**
     * 게시글 생성 API
     * [POST] /posts
     *
     * @return BaseResponse<PostPostsRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostPostsRes> createPost(@RequestPart("content") PostPostsReq postPostsReq,
                                                 @RequestPart("imgUrl") List<MultipartFile> postImage
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

        List<String> imgPaths = null;

        if(postImage.size() > 1){
            imgPaths = s3Service.upload(postImage);
            System.out.println("IMG 경로들 : " + imgPaths);
        }

        try {

            PostPostsRes postPostsRes = postService.createPost(imgPaths, postPostsReq, accessToken);
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
    public BaseResponse<PostDeleteRes> deletePost(@PathVariable("postIdx") Integer postIdx,
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
     * [DELETE] /posts/deleteLike/:postIdx
     * @return BaseResponse<PostLikeRes>
     */
    @ResponseBody
    @DeleteMapping("/deleteLike/{postIdx}")
    public BaseResponse<PostLikeRes> deletePostLike(@PathVariable("postIdx") Integer postIdx, @RequestBody Map<String, Long> userIdx) throws BaseException {

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
            PostLikeRes postLikeRes = postService.deletePostLike(userIdx.get("userIdx"), postIdx, accessToken);
            return new BaseResponse<>(postLikeRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 게시글 저장 API
     * [POST] /posts/save/:postIdx
     * @return BaseResponse<PostSaveRes>
     */
    @ResponseBody
    @PostMapping("/save/{postIdx}")
    public BaseResponse<PostSaveRes> createPostSave(@PathVariable("postIdx") Integer postIdx, @RequestBody Map<String, Long> userIdx) throws BaseException {

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
            PostSaveRes postSaveRes = postService.createPostSave(userIdx.get("userIdx"), postIdx, accessToken);
            return new BaseResponse<>(postSaveRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 게시글 저장 취소 API
     * [POST] /posts/deleteSave/:postIdx
     * @return BaseResponse<PostLikeRes>
     */
    @ResponseBody
    @DeleteMapping("/deleteSave/{postIdx}")
    public BaseResponse<PostSaveRes> deletePostSave(@PathVariable("postIdx") Integer postIdx, @RequestBody Map<String, Long> userIdx) throws BaseException {

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
            PostSaveRes postSaveRes = postService.deletePostSave(userIdx.get("userIdx"), postIdx, accessToken);
            return new BaseResponse<>(postSaveRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 게시글 조회 API
     * [GET] /posts/:postIdx
     * @return BaseResponse<GetPostRes>
     */
    @ResponseBody
    @GetMapping("/{postIdx}")
    public BaseResponse<GetPostRes> getPost(@PathVariable ("postIdx") int postIdx, @RequestBody Map<String, Long> userIdx) {
        try{
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
            GetPostRes getPostRes = postProvider.getPost(userIdx.get("userIdx"), postIdx, accessToken);
            return new BaseResponse<>(getPostRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 관리자 공지 리스트 조회 API
     * [GET] /posts/notification
     * @return BaseResponse<GetNoticeListRes>
     */
    @ResponseBody
    @GetMapping("/notification")
    public BaseResponse<GetPostList> getNoticeList(@RequestBody Map<String, Long> userIdx) {
        try{
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
            GetPostList getNoticeList = postProvider.getNoticeList(userIdx.get("userIdx"), accessToken);
            return new BaseResponse<>(getNoticeList);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 자유게시판 리스트 조회 API
     * [GET] /posts/general
     * @return BaseResponse<GetPostList>
     */
    @ResponseBody
    @GetMapping("/general")
    public BaseResponse<GetPostList> getGeneralList(@RequestBody Map<String, Long> userIdx) {
        try{
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
            GetPostList getGeneralList = postProvider.getGeneralList(userIdx.get("userIdx"), accessToken);
            return new BaseResponse<>(getGeneralList);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 정보게시판 리스트 조회 API
     * [GET] /posts/information
     * @return BaseResponse<GetPostList>
     */
    @ResponseBody
    @GetMapping("/information")
    public BaseResponse<GetPostList> getInformationList(@RequestBody Map<String, Long> userIdx) {
        try{
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
            GetPostList getInformationList = postProvider.getInformationList(userIdx.get("userIdx"), accessToken);
            return new BaseResponse<>(getInformationList);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 장터게시판-나눔 리스트 조회 API
     * [GET] /posts/share
     * @return BaseResponse<GetPostList>
     */
    @ResponseBody
    @GetMapping("/share")
    public BaseResponse<GetPostList> getShareList(@RequestBody Map<String, Long> userIdx) {
        try{
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
            GetPostList getShareList = postProvider.getShareList(userIdx.get("userIdx"), accessToken);
            return new BaseResponse<>(getShareList);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 장터게시판-공동구매 리스트 조회 API
     * [GET] /posts/group
     * @return BaseResponse<GetPostList>
     */
    @ResponseBody
    @GetMapping("/group")
    public BaseResponse<GetPostList> getGroupList(@RequestBody Map<String, Long> userIdx) {
        try{
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
            GetPostList getGroupList = postProvider.getGroupList(userIdx.get("userIdx"), accessToken);
            return new BaseResponse<>(getGroupList);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 장터게시판-중고거래 리스트 조회 API
     * [GET] /posts/secondhand
     * @return BaseResponse<GetPostList>
     */
    @ResponseBody
    @GetMapping("/secondhand")
    public BaseResponse<GetPostList> getSecondhandList(@RequestBody Map<String, Long> userIdx) {
        try{
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
            GetPostList getSecondhandList = postProvider.getSecondhandList(userIdx.get("userIdx"), accessToken);
            return new BaseResponse<>(getSecondhandList);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 내가 작성한 글 조회 API
     * [GET] /posts/myposts
     * @return BaseResponse<GetPostList>
     */
    @ResponseBody
    @GetMapping("/myposts")
    public BaseResponse<GetPostList> getMypostList(@RequestBody Map<String, Long> userIdx) {
        try{
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
            GetPostList getMypostList = postProvider.getMypostList(userIdx.get("userIdx"), accessToken);
            return new BaseResponse<>(getMypostList);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 내가 스크랩한 글 조회 API
     * [GET] /posts/mysaves
     * @return BaseResponse<GetPostList>
     */
    @ResponseBody
    @GetMapping("/mysaves")
    public BaseResponse<GetPostList> getMysaveList(@RequestBody Map<String, Long> userIdx) {
        try{
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
            GetPostList getMysaveList = postProvider.getMysaveList(userIdx.get("userIdx"), accessToken);
            return new BaseResponse<>(getMysaveList);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 내가 좋아요한 글 조회 API
     * [GET] /posts/mylikes
     * @return BaseResponse<GetPostList>
     */
    @ResponseBody
    @GetMapping("/mylikes")
    public BaseResponse<GetPostList> getMylikeList(@RequestBody Map<String, Long> userIdx) {
        try{
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
            GetPostList getMylikeList = postProvider.getMylikeList(userIdx.get("userIdx"), accessToken);
            return new BaseResponse<>(getMylikeList);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
