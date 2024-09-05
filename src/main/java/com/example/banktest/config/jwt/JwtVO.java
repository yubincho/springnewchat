package com.example.banktest.config.jwt;

public interface JwtVO {

    public static final String SECRET = "메타코딩";  // HS256 대칭키
    public static final int EXPIRATION_TIME = 1000 * 60 * 10; // 10분 (1/1000초)
    public static final int REFRESH_TIME = 1000 * 60 * 15; // 15분 (1/1000초)

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";

}
