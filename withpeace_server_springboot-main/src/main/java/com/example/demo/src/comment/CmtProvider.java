package com.example.demo.src.comment;

import com.example.demo.config.BaseException;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

//Provider : Read의 비즈니스 로직 처리
@Service
public class CmtProvider {

    private final CmtDao cmtDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public CmtProvider(CmtDao cmtDao, JwtService jwtService) {
        this.cmtDao = cmtDao;
        this.jwtService = jwtService;
    }

    /** 댓글 작성자 확인 **/
    public Long checkCmtUser(Integer commentIdx) throws BaseException{
        try{
            return cmtDao.checkCmtUser(commentIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
