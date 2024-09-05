package com.example.banktest.domain.jwt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
//
//    Optional<RefreshToken> findByUserId(Long userId); // userId 타입이 Long 인데 에러남.
//    Optional<RefreshToken> findByRefreshToken(String refreshToken);
//}

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisRefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String USER_KEY_PREFIX = "refresh_token:user:";
    private static final String TOKEN_KEY_PREFIX = "refresh_token:token:";

    public RedisRefreshTokenRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(Long userId, String refreshToken, long expirationTime) {
        String userKey = USER_KEY_PREFIX + userId;
        String tokenKey = TOKEN_KEY_PREFIX + refreshToken;

        redisTemplate.opsForValue().set(userKey, refreshToken, expirationTime, TimeUnit.MILLISECONDS);
        redisTemplate.opsForValue().set(tokenKey, userId.toString(), expirationTime, TimeUnit.MILLISECONDS);
    }

    public String findByUserId(Long userId) {
        String key = USER_KEY_PREFIX + userId;
        return redisTemplate.opsForValue().get(key);
    }

    public String findUserIdByRefreshToken(String refreshToken) {
        String key = TOKEN_KEY_PREFIX + refreshToken;
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteByUserId(Long userId) {
        String userKey = USER_KEY_PREFIX + userId;
        String refreshToken = redisTemplate.opsForValue().get(userKey);
        if (refreshToken != null) {
            String tokenKey = TOKEN_KEY_PREFIX + refreshToken;
            redisTemplate.delete(userKey);
            redisTemplate.delete(tokenKey);
        }
    }

    public void deleteByRefreshToken(String refreshToken) {
        String tokenKey = TOKEN_KEY_PREFIX + refreshToken;
        String userId = redisTemplate.opsForValue().get(tokenKey);
        if (userId != null) {
            String userKey = USER_KEY_PREFIX + userId;
            redisTemplate.delete(userKey);
            redisTemplate.delete(tokenKey);
        }
    }
}