//package com.example.banktest.web;
//
//import com.example.banktest.config.dummy.DummyObject;
//import com.example.banktest.domain.entity.User;
//import com.example.banktest.domain.user.UserRepository;
//import com.example.banktest.dto.user.UserReqDto;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//
//@AutoConfigureMockMvc
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//public class UserApiControllerTest extends DummyObject {
//
//    private final Logger log = LoggerFactory.getLogger(getClass());
//    private static final String APPLICATION_JSON_UTF8 = "application/json; charset=utf-8";
//
//    @Autowired
//    private MockMvc mvc;
//    @Autowired
//    private ObjectMapper om;
//
//    @Autowired
//    private UserRepository userRepository;
//
//
//    @BeforeEach
//    public void setUp() {
//        dataSetting();
//    }
//
//    @Test
//    public void join_success_test() throws Exception {
//        // given
//        UserReqDto.JoinReqDto joinReqDto = new UserReqDto.JoinReqDto();
//        joinReqDto.setUsername("love");
//        joinReqDto.setPassword("1234");
//        joinReqDto.setEmail("love@nate.com");
//        joinReqDto.setFullname("러브");
//
//        String requestBody = om.writeValueAsString(joinReqDto);
//        log.debug("테스트 : " + requestBody);
//
//        // when
//        ResultActions resultActions = mvc.perform(post("/api/join").content(requestBody).contentType(APPLICATION_JSON_UTF8));
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        System.out.println("테스트 : " + responseBody);
//
//        // then
//        resultActions.andExpect(status().isCreated());
//    }
//
//
//    @Test
//    public void join_fail_test() throws Exception {
//        // given
//        UserReqDto.JoinReqDto joinReqDto = new UserReqDto.JoinReqDto();
//        joinReqDto.setUsername("love");
//        joinReqDto.setPassword("1234");
//        joinReqDto.setEmail("love@nate.com");
//        joinReqDto.setFullname("러브");
//
//        String requestBody = om.writeValueAsString(joinReqDto);
//        log.debug("테스트 : " + requestBody);
//
//        // when
//        ResultActions resultActions = mvc.perform(post("/api/join").content(requestBody).contentType(APPLICATION_JSON_UTF8));
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        System.out.println("테스트 : " + responseBody);
//
//        // then
//        resultActions.andExpect(status().isCreated());
//    }
//
//
//    private void dataSetting() {
//        User ssar = userRepository.save(newUser("ssar", "쌀"));
//        User cos = userRepository.save(newUser("cos", "코스"));
//        User admin = userRepository.save(newUser("admin", "관리자"));
//    }
//}
