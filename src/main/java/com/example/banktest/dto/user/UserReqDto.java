package com.example.banktest.dto.user;

import com.example.banktest.domain.entity.User;
import com.example.banktest.domain.entity.UserEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;



public class UserReqDto {

    @Getter
    @Setter
    public static class LoginReqDto {
        private String username;
        private String password;
    }


    @Setter
    @Getter
    public static class JoinReqDto {

        // 영문,숫자만 되고, 길이는 최소2~20이다.
        @NotEmpty(message = "username은 필수입니다")
        @Pattern(regexp = "^[a-zA-Z0-9]{2,20}$", message = "영문/숫자 2~20자 이내로 작성해주세요.")
        private String username;

        @NotEmpty(message = "password는 필수입니다")
        @Size(min = 4, max = 20) // 패스워드 인코딩 때문에
        private String password;

        @NotEmpty(message = "email은 필수입니다")
        @Size(min = 9, max = 20)
        @Pattern(regexp = "^[a-zA-Z0-9.]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,3}$", message = "이메일 형식으로 적어주세요")
        private String email;

        @NotEmpty(message = "fullname은 필수입니다")
        @Pattern(regexp = "^[a-zA-Z가-힣]{1,20}$", message = "한글/영문 1~20자 이내로 작성해주세요.")
        private String nickname;

        public User toEntity(BCryptPasswordEncoder passwordEncoder) {
            return User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .email(email)
                    .nickname(nickname)
                    .role(UserEnum.CUSTOMER)
                    .build();
        }
    }



}
