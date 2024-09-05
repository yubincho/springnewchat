package com.example.banktest.service;


import com.example.banktest.domain.user.User;
import com.example.banktest.domain.user.UserRepository;
import com.example.banktest.dto.user.UserRespDto;
import com.example.banktest.dto.user.UserReqDto;
import com.example.banktest.handler.ex.CustomApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    @Transactional
    public UserRespDto.JoinRespDto 회원가입(UserReqDto.JoinReqDto joinReqDto) {
        // 1. 동일 username 존재
        Optional<User> user = userRepository.findByUsername(joinReqDto.getUsername());
        if (user.isPresent()) {
            throw new CustomApiException("동일한 username 이 존재합니다.");
        }

        // 2. 패스워드 인코딩 + 회원가입
        User userPs = userRepository.save(joinReqDto.toEntity(passwordEncoder));

        // 3. dto 응답
        return new UserRespDto.JoinRespDto(userPs);
    }


    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Unexpected User"));
    }


    public  User findByUserName(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Unexpected User"));
    }

}




