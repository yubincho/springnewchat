package com.example.banktest.domain.chat;

import com.example.banktest.domain.entity.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ChatMessageRepository {

    private final RedisTemplate<String, ChatMessage> redisTemplate;

    @Autowired
    public ChatMessageRepository(RedisTemplate<String, ChatMessage> redisTemplate) {
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