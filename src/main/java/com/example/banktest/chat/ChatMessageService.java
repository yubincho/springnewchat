//package com.example.banktest.chat;
//
//import com.example.banktest.chat.repository.ChatMessage;
//import com.example.banktest.chat.repository.ChatMessageRepository;
//import com.example.banktest.chat.repository.ChatRoomRepository;
//import com.example.banktest.chat.repository.MessageType;
//import com.example.banktest.domain.user.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//
//@RequiredArgsConstructor
//@Service
//public class ChatMessageService {
//
//    private final ChatMessageRepository chatMessageRepository;
//    private final UserRepository userRepository;
//    private final ChatRoomRepository chatRoomRepository;
//
//
//    public ChatMessage processChatMessage(String sessionId, String content, String username, String chatRoomId) {
//        var user = userRepository.findByUsername(username);
//        var chatRoom = chatRoomRepository.findById(chatRoomId);
//
//        if (user.isEmpty()) {
//            throw new IllegalArgumentException("Invalid user: " + username);
//        }
//
//        if (chatRoom.isEmpty()) {
//            throw new IllegalArgumentException("Invalid chat room ID: " + chatRoomId);
//        }
//
//        ChatMessage chatMessage = ChatMessage.builder()
//                .messageType(MessageType.CHAT)
//                .sender(username)
//                .chatRoomId(chatRoomId)
//                .sessionId(sessionId)
//                .content(content)
//                .build();
//
//        chatMessageRepository.save(chatMessage);
//
//        return chatMessage;
//    }
//
//}
