package com.example.banktest.web;


import com.example.banktest.dto.ResponseDto;
import com.example.banktest.dto.user.UserRespDto;
import com.example.banktest.dto.user.UserReqDto;
import com.example.banktest.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

import com.example.banktest.config.auth.LoginUser;
import com.example.banktest.domain.entity.User;
import com.example.banktest.dto.user.UserRespDto;


@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class UserController {

    private final UserService userService;


    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid UserReqDto.JoinReqDto joinReqDto, BindingResult bindingResult) {

        UserRespDto.JoinRespDto userRespDto = userService.회원가입(joinReqDto);
        return new ResponseEntity<>(new ResponseDto<>(1, "회원가입 성공", userRespDto), HttpStatus.CREATED);
    }



    @GetMapping("/user/current")
    public ResponseEntity<UserRespDto.LoginRespDto> getCurrentUser(@AuthenticationPrincipal LoginUser loginUser) {
        if (loginUser == null) {
            return ResponseEntity.notFound().build();
        }

        User user = loginUser.getUser();
        UserRespDto.LoginRespDto responseDto = new UserRespDto.LoginRespDto(user);

        return ResponseEntity.ok(responseDto);
    }



}


