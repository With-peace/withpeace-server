package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.post.model.*;
import com.example.demo.src.post.*;
import com.example.demo.src.auth.*;
import com.example.demo.src.user.*;
import com.example.demo.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;

import java.lang.*;
import java.sql.SQLException;
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
}
