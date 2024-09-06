package com.example.banktest.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.example.banktest.config.auth.LoginUser;
import com.example.banktest.domain.entity.User;
import com.example.banktest.domain.entity.UserEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class JwtProcess {

    private static final Logger log = LoggerFactory.getLogger(JwtProcess.class);

    // 토큰 생성
    // 토큰 자체는 암호화 X ! 때무에 많은 정보 넣지 X !, id랑 role 정도만 넣을 것 !
    public static String create(LoginUser loginUser) {
        log.debug("디버그 : JwtProcess create()");
        String jwtToken = JWT.create()
                .withSubject(loginUser.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtVO.EXPIRATION_TIME))
                .withClaim("id", loginUser.getUser().getId())
                .withClaim("role", loginUser.getUser().getRole().name())
                .withClaim("username", loginUser.getUsername())
                .sign(Algorithm.HMAC512(JwtVO.SECRET));
        return JwtVO.TOKEN_PREFIX + jwtToken;
    }

    // 토큰 검증
    // return 되는 LoginUser 객체를 강제로 시큐리티 세션에 직접 주입할 예정
    public static LoginUser verify(String token) {
        log.debug("디버그 : JwtProcess verify()");
        System.out.println("[raw token] : " + token);
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(JwtVO.SECRET)).build().verify(token);
            System.out.println("[decodedJWT] : " + decodedJWT);

            Long id = decodedJWT.getClaim("id").asLong();
            String role = decodedJWT.getClaim("role").asString();
            String username = decodedJWT.getClaim("username").asString();

            User user = User.builder()
                    .id(id)
                    .role(UserEnum.valueOf(role))
                    .username(username)
                    .build();

            System.out.println("[user] : " + user);
//            LoginUser loginUser = new LoginUser(user);
            System.out.println("[login user 1] : " + new LoginUser(user));
            log.debug("디버그 : login user" + new LoginUser(user));
            System.out.println("[login user 2] : " + new LoginUser(user));
            return new LoginUser(user);
        }
        catch (Exception e) {
            log.error("JWT 토큰 검증 실패: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT Token", e);
        }
    }


    // 토큰의 만료 여부를 검증
    public boolean isTokenExpired(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(JwtVO.SECRET)).build().verify(token);
            return decodedJWT.getExpiresAt().before(new Date());
        } catch (Exception e) {
            return true; // 만료되었거나 유효하지 않은 토큰인 경우
        }
    }



    // Refresh 토큰 생성
    public static String makeRefreshToken(LoginUser loginUser) {
        log.debug("디버그 : JwtProcess makeRefreshToken()");
        String jwtToken = JWT.create()
                .withSubject(loginUser.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtVO.REFRESH_TIME))
                .withClaim("id", loginUser.getUser().getId())
                .withClaim("role", loginUser.getUser().getRole().name())
                .withClaim("username", loginUser.getUsername())
                .sign(Algorithm.HMAC512(JwtVO.SECRET));
        return JwtVO.TOKEN_PREFIX + jwtToken;
    }
}
