package com.example.banktest.dto.user;

import com.example.banktest.domain.entity.User;
import com.example.banktest.util.CustomDateUtil;
import lombok.Getter;
import lombok.Setter;


public class UserRespDto {

    @Setter
    @Getter
    public static class LoginRespDto {
        private Long id;
        private String username;
        private String createdAt;

        public LoginRespDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.createdAt = CustomDateUtil.toStringFormat(user.getCreatedAt());
        }

    }

    @Setter
    @Getter
    public static class JoinRespDto {
        private Long id;
        private String username;
        private String nickname;

        public JoinRespDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.nickname = user.getNickname();
        }
    }
}
