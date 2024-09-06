package com.example.banktest.config.auth;

import com.example.banktest.domain.entity.User;
import com.example.banktest.domain.user.UserRepository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;


// CustomUserDetailsService

@RequiredArgsConstructor
@Service
public class LoginService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(getClass());

//    @Autowired
    private final UserRepository userRepository;


    // 시큐리티 로그인이 될 때, 시큐리티가 loadUserByUsername() 실행해서 username 을 체크 !
    // 없으면 오류
    // 있으면 정상적으로 시큐리티 컨택스트 내부 세션에 로그인된 세션이 만들어진다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userPS = userRepository.findByUsername(username).orElseThrow(
                () -> new InternalAuthenticationServiceException("인증 실패")
        );
        log.debug("로드된 사용자: {}", userPS.getUsername()); // 로그 추가
        return new LoginUser(userPS); // 찾으면 LoginUser 실행
    }
}
