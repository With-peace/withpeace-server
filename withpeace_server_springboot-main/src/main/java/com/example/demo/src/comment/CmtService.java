package com.example.demo.src.comment;


import com.example.demo.config.BaseException;
import com.example.demo.src.auth.AuthDao;
import com.example.demo.src.comment.model.*;
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

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class CmtService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CmtDao cmtDao;
    private final CmtProvider cmtProvider;
    private final JwtService jwtService;
    private DataSource dataSource;


    @Autowired
    public CmtService(CmtDao cmtDao, CmtProvider cmtProvider, JwtService jwtService) {
        this.cmtDao = cmtDao;
        this.cmtProvider = cmtProvider;
        this.jwtService = jwtService;

    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }


}
