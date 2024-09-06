package com.example.banktest.service.jwt_token;

import com.example.banktest.domain.jwt.RedisRefreshTokenRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final RedisRefreshTokenRepository redisRefreshTokenRepository;

    public Long findUserIdByRefreshToken(String refreshToken) {
        String userId = redisRefreshTokenRepository.findUserIdByRefreshToken(refreshToken);
        if (userId == null) {
            throw new IllegalArgumentException("Unexpected Token");
        }
        return Long.parseLong(userId);
    }

    public void saveRefreshToken(Long userId, String refreshToken, long expirationTime) {
        redisRefreshTokenRepository.save(userId, refreshToken, expirationTime);
    }

    public void deleteRefreshToken(String refreshToken) {
        redisRefreshTokenRepository.deleteByRefreshToken(refreshToken);
    }

    public boolean validateRefreshToken(String refreshToken) {
        return redisRefreshTokenRepository.findUserIdByRefreshToken(refreshToken) != null;
    }
}
