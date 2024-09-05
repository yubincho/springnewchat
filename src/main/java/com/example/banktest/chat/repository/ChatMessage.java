package com.example.banktest.chat.repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@RedisHash("ChatMessage")
public class ChatMessage {

    @Id
    private String id;
    private String sessionId;
    private String content;
    private MessageType messageType;
    private String sender;
    private Long chatRoomId;
    private Long userId;


}
