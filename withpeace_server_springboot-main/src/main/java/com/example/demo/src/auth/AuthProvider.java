package com.example.demo.src.auth;

import com.example.demo.config.BaseException;
import com.example.demo.src.auth.*;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class AuthProvider {
    private final AuthDao authDao;
    private final AuthService authService;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public AuthProvider(AuthDao authDao, AuthService authService, JwtService jwtService) {
        this.authDao = authDao;
        this.authService = authService;
        this.jwtService = jwtService;
    }
}
