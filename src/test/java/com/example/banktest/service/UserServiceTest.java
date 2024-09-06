package com.example.banktest.service;

import com.example.banktest.config.dummy.DummyObject;
import com.example.banktest.domain.user.UserRepository;
import com.example.banktest.dto.user.UserRespDto;
import com.example.banktest.dto.user.UserReqDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.banktest.domain.entity.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class) //service test 할 때
class UserServiceTest extends DummyObject {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Spy  // 진짜꺼 넣고 싶은 것
    private BCryptPasswordEncoder passwordEncoder;


    @Test
    void 회원가입_test() {
        // given
        UserReqDto.JoinReqDto joinReqDto = new UserReqDto.JoinReqDto();
        joinReqDto.setUsername("ssar");
        joinReqDto.setPassword("1111");
        joinReqDto.setEmail("ssar@gmail.com");
        joinReqDto.setFullname("쌀");

        // stub 1
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
//        when(userRepository.findByUsername(any())).thenReturn(Optional.of(new User())); // 동일한 username 이 존재합니다.

        // stub 2
        User ssar = newMockUser(1L, "ssar", "쌀");
        when(userRepository.save(any())).thenReturn(ssar);;

        // when
        UserRespDto.JoinRespDto joinResDto = userService.회원가입(joinReqDto);
        System.out.println("joinResDto : " + joinResDto);

        // then
        assertThat(joinResDto.getId()).isEqualTo(1L);
        assertThat(joinResDto.getUsername()).isEqualTo("ssar");
    }



}