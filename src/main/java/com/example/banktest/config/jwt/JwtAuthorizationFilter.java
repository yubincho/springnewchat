package com.example.banktest.config.jwt;

import com.example.banktest.config.auth.LoginUser;
import com.example.banktest.domain.jwt.RedisRefreshTokenRepository;
import com.example.banktest.util.CustomResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthorizationFilter.class);
    private final JwtProcess jwtProcess;
    private final RedisRefreshTokenRepository redisRefreshTokenRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager,
                                  JwtProcess jwtProcess,
                                  RedisRefreshTokenRepository redisRefreshTokenRepository) {
        super(authenticationManager);
        this.jwtProcess = jwtProcess;
        this.redisRefreshTokenRepository = redisRefreshTokenRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader(JwtVO.HEADER);

        if (header != null && header.startsWith(JwtVO.TOKEN_PREFIX)) {
            String token = header.replace(JwtVO.TOKEN_PREFIX, "");
            try {
                LoginUser loginUser = jwtProcess.verify(token);
                if (loginUser != null && isRefreshTokenValid(loginUser.getUser().getId())) {
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            loginUser, null, loginUser.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    log.warn("Invalid JWT token or refresh token.");
                    CustomResponseUtil.fail(response, "로그인을 해주세요.", HttpStatus.UNAUTHORIZED);
                    return;
                }
            } catch (Exception e) {
                log.warn("JWT 토큰 검증 실패: {}", e.getMessage());
                CustomResponseUtil.fail(response, "로그인을 해주세요.", HttpStatus.UNAUTHORIZED);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isRefreshTokenValid(Long userId) {
        String refreshToken = redisRefreshTokenRepository.findByUserId(userId);
        return refreshToken != null && !jwtProcess.isTokenExpired(refreshToken.replace(JwtVO.TOKEN_PREFIX, ""));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/ws") || path.startsWith("/css") || path.startsWith("/js") || path.startsWith("/images")
                || path.startsWith("/login") || path.startsWith("/api/join");
    }
}