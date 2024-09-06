package com.example.banktest.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@RedisHash("ChatMessage")
@JsonSerialize
@JsonDeserialize
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String sessionId;
    private String content;
    private MessageType messageType;
    private String sender;
    private Long chatRoomId;
    private Long userId;


}
