package com.example.banktest.web;


import ch.qos.logback.core.model.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@RequiredArgsConstructor
@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "login";

    }


    @GetMapping("/chat")
    public String chatRoom() {
        // 필요하면 모델에 데이터를 추가
        return "index2.html";  //
    }



}
