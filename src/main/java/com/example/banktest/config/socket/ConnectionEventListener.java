package com.example.banktest.config.socket;

import com.example.banktest.chat.repository.ChatMessage;
import com.example.banktest.chat.repository.ChatMessageRepository;
import com.example.banktest.chat.repository.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;


@RequiredArgsConstructor
@Component
public class ConnectionEventListener {

    private final SimpMessageSendingOperations messageTemplate;
    private final ChatMessageRepository chatMessageRepository;


    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        Long chatRoomId = (Long) headerAccessor.getSessionAttributes().get("chatRoomId");

        if (username != null && chatRoomId != null) {
            ChatMessage chatMessage = ChatMessage.builder()
                    .messageType(MessageType.LEAVE)
                    .sender(username)
                    .chatRoomId(chatRoomId)
                    .content(username + " left the chat")
                    .build();

            chatMessageRepository.save(chatMessage);
            messageTemplate.convertAndSend("/topic/chat/" + chatRoomId, chatMessage);
        }
    }
}