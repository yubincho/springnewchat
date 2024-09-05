package com.example.banktest.security;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.assertj.core.api.Assertions.assertThat;


@AutoConfigureMockMvc  // Mock(가짜 환경)에 MockMvc 가 등록됨
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class SecurityConfigTest {

    // 가짜 환경에 등록된 MockMvc 를 DI 함.
    @Autowired
    private MockMvc mvc;

    @Test
    void authentication_test() throws Exception {
        //given

        // when
        ResultActions resultActions = mvc.perform(get("/api/s/hello"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("test " + responseBody);
        int HttpStatusCode = resultActions.andReturn().getResponse().getStatus(); // => 예쁘게 메시지를 포장하는 공통적인 응답 DTO를 만들기
        System.out.println("test status code " + HttpStatusCode); // 401

        // then
        assertThat(HttpStatusCode).isEqualTo(401);

    }


    @Test
    void authorization_test() throws Exception {
        //given

        // when
        ResultActions resultActions = mvc.perform(get("/api/admin/hello"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("test " + responseBody);
        int HttpStatusCode = resultActions.andReturn().getResponse().getStatus(); // => 예쁘게 메시지를 포장하는 공통적인 응답 DTO를 만들기
        System.out.println("test status code " + HttpStatusCode); // 401

        // then
        assertThat(HttpStatusCode).isEqualTo(401);


    }




}
