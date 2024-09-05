package com.example.banktest.chat.controller;

import com.example.banktest.chat.repository.ChatMessage;
import com.example.banktest.chat.repository.ChatMessageRepository;
import com.example.banktest.chat.repository.MessageType;
import com.example.banktest.config.auth.LoginUser;
import com.example.banktest.config.jwt.JwtProcess;
import com.example.banktest.config.jwt.JwtVO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final JwtProcess jwtProcess;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        String token = headerAccessor.getFirstNativeHeader("Authorization");
        if (token != null && token.startsWith(JwtVO.TOKEN_PREFIX)) {
            token = token.substring(JwtVO.TOKEN_PREFIX.length());
            try {
                LoginUser loginUser = jwtProcess.verify(token);
                chatMessage.setSender(loginUser.getUsername());
                chatMessageRepository.save(chatMessage);
                messagingTemplate.convertAndSend("/topic/chat/" + chatMessage.getChatRoomId(), chatMessage);
            } catch (Exception e) {
                // 토큰 검증 실패
                messagingTemplate.convertAndSendToUser(headerAccessor.getSessionId(), "/queue/errors", "Invalid token");
            }
        } else {
            // 토큰이 없거나 형식이 잘못됨
            messagingTemplate.convertAndSendToUser(headerAccessor.getSessionId(), "/queue/errors", "Token required");
        }
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        String token = headerAccessor.getFirstNativeHeader("Authorization");
        if (token != null && token.startsWith(JwtVO.TOKEN_PREFIX)) {
            token = token.substring(JwtVO.TOKEN_PREFIX.length());
            try {
                LoginUser loginUser = jwtProcess.verify(token);
                String username = loginUser.getUsername();
                headerAccessor.getSessionAttributes().put("username", username);
                headerAccessor.getSessionAttributes().put("chatRoomId", chatMessage.getChatRoomId());
                chatMessage.setMessageType(MessageType.JOIN);
                chatMessage.setSender(username);
                chatMessageRepository.save(chatMessage);
                messagingTemplate.convertAndSend("/topic/chat/" + chatMessage.getChatRoomId(), chatMessage);
            } catch (Exception e) {
                // 토큰 검증 실패
                messagingTemplate.convertAndSendToUser(headerAccessor.getSessionId(), "/queue/errors", "Invalid token");
            }
        } else {
            // 토큰이 없거나 형식이 잘못됨
            messagingTemplate.convertAndSendToUser(headerAccessor.getSessionId(), "/queue/errors", "Token required");
        }
    }
}