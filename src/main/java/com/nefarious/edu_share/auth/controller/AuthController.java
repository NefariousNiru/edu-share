package com.nefarious.edu_share.auth.controller;

import com.nefarious.edu_share.auth.service.AuthService;
import com.nefarious.edu_share.auth.util.Endpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoint.AUTH)
@RequiredArgsConstructor
public class AuthController {
    private AuthService authService;
//    public class Endpoint {
//        public static final String AUTH                         = "/auth";
//        public static final String SIGNUP_V1                    = "/signup";
//        public static final String SIGNIN_V1                    = "/signin";
//        public static final String VERIFY_V1                    = "/verify";
//        public static final String LOGOUT_V1                    = "/logout";
//        public static final String RESEND_OTP_V1                = "/resend-otp";
//        public static final String REFRESH_SESSION_V1           = "/refresh";
//        public static final String FORGOT_PASSWORD_V1           = "/forgot-password";
//    }
}
