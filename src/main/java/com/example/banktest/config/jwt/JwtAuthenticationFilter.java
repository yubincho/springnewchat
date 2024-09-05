package com.example.banktest.config.jwt;

import com.example.banktest.config.auth.LoginUser;
import com.example.banktest.domain.jwt.RedisRefreshTokenRepository;
import com.example.banktest.dto.user.UserReqDto;
import com.example.banktest.dto.user.UserRespDto;
import com.example.banktest.service.jwt_token.RefreshTokenService;
import com.example.banktest.util.CustomResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final AuthenticationManager authenticationManager;
    private final JwtProcess jwtProcess;
    private final RefreshTokenService refreshTokenService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   JwtProcess jwtProcess,
                                   RefreshTokenService refreshTokenService, RedisRefreshTokenRepository refreshTokenRepository) {
        super(authenticationManager);
        setFilterProcessesUrl("/api/login");
        this.authenticationManager = authenticationManager;
        this.jwtProcess = jwtProcess;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        log.debug("JwtAuthenticationFilter: attemptAuthentication 시작");
        try {
            ObjectMapper om = new ObjectMapper();
            UserReqDto.LoginReqDto loginReqDto = om.readValue(request.getInputStream(), UserReqDto.LoginReqDto.class);
            log.debug("Login attempt for user: {}", loginReqDto.getUsername());

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginReqDto.getUsername(),
                    loginReqDto.getPassword());

            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            log.debug("Authentication successful for user: {}", loginReqDto.getUsername());
            return authentication;
        } catch (IOException e) {
            log.error("Login request parsing failed", e);
            throw new InternalAuthenticationServiceException("로그인 요청 처리 중 오류가 발생했습니다.", e);
        } catch (AuthenticationException e) {
            log.error("Authentication failed", e);
            throw e;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        log.debug("JwtAuthenticationFilter: successfulAuthentication 시작");
        LoginUser loginUser = (LoginUser) authResult.getPrincipal();

        String jwtToken = jwtProcess.create(loginUser);
        String refreshToken = jwtProcess.makeRefreshToken(loginUser);

        // JWT 토큰과 리프레시 토큰을 응답 헤더에 추가
        response.addHeader(JwtVO.HEADER, JwtVO.TOKEN_PREFIX + jwtToken);
        response.addHeader("Refresh-Token", JwtVO.TOKEN_PREFIX + refreshToken);

        // 리프레시 토큰을 저장
        refreshTokenService.saveRefreshToken(loginUser.getUser().getId(), refreshToken, JwtVO.REFRESH_TIME);

        // 성공 응답 전송
        UserRespDto.LoginRespDto loginRespDto = new UserRespDto.LoginRespDto(loginUser.getUser());
        CustomResponseUtil.success(response, loginRespDto);
        log.debug("Login successful for user: {}", loginUser.getUsername());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        log.error("Login failed", failed);
        CustomResponseUtil.fail(response, "로그인 실패: " + failed.getMessage(), HttpStatus.UNAUTHORIZED);
    }

}