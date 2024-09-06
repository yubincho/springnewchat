package com.example.banktest.chat;

import com.example.banktest.domain.chat.*;
import com.example.banktest.domain.entity.ChatMessage;
import com.example.banktest.domain.entity.ChatRoom;
import com.example.banktest.domain.entity.MessageType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import com.example.banktest.config.auth.LoginUser;
import com.example.banktest.config.jwt.JwtProcess;
import com.example.banktest.config.jwt.JwtVO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final JwtProcess jwtProcess;
    private final ChatRoomRepository chatRoomRepository;


    // 채팅방 페이지로 이동하는 메서드
    @GetMapping("/chat/{roomId}")
    public String chatRoom(@PathVariable String roomId, @RequestParam String roomName, Model model) {
        model.addAttribute("roomId", roomId);
        model.addAttribute("roomName", roomName);
        return "chat";  // chat.html 파일을 반환
    }

    @PostMapping("/api/chatroom")
    public ResponseEntity<Optional<ChatRoom>> createChatRoom(@RequestBody Map<String, String> params,
                                                             @AuthenticationPrincipal LoginUser loginUser) {
        String roomName = params.get("name");
        if (roomName == null || roomName.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // 같은 이름의 방이 있는지 확인
        Optional<ChatRoom> existingRoom = chatRoomRepository.findByName(roomName);
        if (existingRoom.isPresent()) {
            // 기존 방이 있으면 그 방의 정보를 반환
            return ResponseEntity.ok(existingRoom);
        }

        // 새 방 생성
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(roomName);
        chatRoom.setCreatedBy(loginUser.getUser());

        chatRoomRepository.save(chatRoom);

        return ResponseEntity.ok(Optional.of(chatRoom));
    }


    @GetMapping
    public ResponseEntity<List<ChatRoom>> getChatRooms() {
        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
        return ResponseEntity.ok(chatRooms);
    }



//    @MessageMapping("/chat.sendMessage")
//    public void sendMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
//        System.out.println("Received message: " + chatMessage.getContent());
//        System.out.println("[chatMessage]" + chatMessage);
//        String token = headerAccessor.getFirstNativeHeader("Authorization");
//        if (token != null && token.startsWith(JwtVO.TOKEN_PREFIX)) {
//            token = token.substring(JwtVO.TOKEN_PREFIX.length());
//            try {
//                LoginUser loginUser = jwtProcess.verify(token);
//                System.out.println("Token verified for user: " + loginUser.getUsername());  //
//                chatMessage.setSender(loginUser.getUsername());
//                chatMessageRepository.save(chatMessage);
//                messagingTemplate.convertAndSend("/topic/chat/" + chatMessage.getChatRoomId(), chatMessage);
//                System.out.println("Message broadcasted to: /topic/chat/" + chatMessage.getChatRoomId());
//            } catch (Exception e) {
//                // 토큰 검증 실패
//                messagingTemplate.convertAndSendToUser(headerAccessor.getSessionId(), "/queue/errors", "Invalid token");
//            }
//        } else {
//            // 토큰이 없거나 형식이 잘못됨
//            messagingTemplate.convertAndSendToUser(headerAccessor.getSessionId(), "/queue/errors", "Token required");
//        }
//    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        System.out.println("Received message: " + chatMessage.getContent());
        System.out.println("[chatMessage]" + chatMessage);
        String token = headerAccessor.getFirstNativeHeader("Authorization");
        System.out.println("Token: " + (token != null ? "present" : "null"));
        if (token != null && token.startsWith(JwtVO.TOKEN_PREFIX)) {
            token = token.substring(JwtVO.TOKEN_PREFIX.length());
            try {
                LoginUser loginUser = jwtProcess.verify(token);
                System.out.println("Token verified for user: " + loginUser.getUsername());
                chatMessage.setSender(loginUser.getUsername());
                chatMessage.setUserId(loginUser.getUser().getId());  // 사용자 ID 설정
                try {
                    chatMessageRepository.save(chatMessage);
                    System.out.println("Message saved to repository");
                } catch (Exception e) {
                    System.err.println("Error saving message to repository: " + e.getMessage());
                    e.printStackTrace();
                }
                try {
                    messagingTemplate.convertAndSend("/topic/chat/" + chatMessage.getChatRoomId(), chatMessage);
                    System.out.println("Message broadcasted to: /topic/chat/" + chatMessage.getChatRoomId());
                } catch (Exception e) {
                    System.err.println("Error broadcasting message: " + e.getMessage());
                    e.printStackTrace();
                }
            } catch (Exception e) {
                System.err.println("Error verifying token: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("Token is null or invalid format");
        }
        System.out.println("End of sendMessage method");
    }



    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        String token = headerAccessor.getFirstNativeHeader("Authorization");
        if (token != null && token.startsWith(JwtVO.TOKEN_PREFIX)) {
            token = token.substring(JwtVO.TOKEN_PREFIX.length());
            try {
                LoginUser loginUser = jwtProcess.verify(token);
                String username = loginUser.getUsername();
                Long userId = loginUser.getUser().getId();
                headerAccessor.getSessionAttributes().put("username", username);
                headerAccessor.getSessionAttributes().put("userId", userId);
                headerAccessor.getSessionAttributes().put("chatRoomId", chatMessage.getChatRoomId());
                System.out.println("User " + username + " joining room: " + chatMessage.getChatRoomId());
                chatMessage.setMessageType(MessageType.JOIN);
                chatMessage.setContent(username + " joined the chat"); //
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