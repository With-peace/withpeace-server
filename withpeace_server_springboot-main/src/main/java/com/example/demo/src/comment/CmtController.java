package com.example.demo.src.comment;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.comment.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.TokenVerify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

}
