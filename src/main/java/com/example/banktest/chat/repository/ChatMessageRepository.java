package com.example.banktest.chat.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ChatMessageRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public ChatMessageRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(ChatMessage chatMessage) {
        if (chatMessage.getId() == null) {
            chatMessage.setId(UUID.randomUUID().toString());
        }
        redisTemplate.opsForHash().put("ChatMessage", chatMessage.getId(), chatMessage);
    }

    public ChatMessage findById(String id) {
        return (ChatMessage) redisTemplate.opsForHash().get("ChatMessage", id);
    }
}


