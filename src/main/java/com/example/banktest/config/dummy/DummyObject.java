//package com.example.banktest.config.dummy;
//
//import com.example.banktest.domain.entity.User;
//import com.example.banktest.domain.entity.UserEnum;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//import java.time.LocalDateTime;
//
//public class DummyObject {
//
//    protected User newUser(String username, String nickname) {
//
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        String encPassword = passwordEncoder.encode("1234");
//
//        return User.builder()
//                .username(username)
//                .password(encPassword)
//                .email(username + "@gmail.com")
//                .nickname(nickname)
//                .role(UserEnum.CUSTOMER)
//                .build();
//    }
//
//
//    protected User newMockUser(Long id, String username, String nickname) {
//
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        String encPassword = passwordEncoder.encode("1234");
//
//        return User.builder()
//                .id(id)
//                .username(username)
//                .password(encPassword)
//                .email(username + "@gmail.com")
//                .nickname(nickname)
//                .role(UserEnum.CUSTOMER)
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//    }
//}
