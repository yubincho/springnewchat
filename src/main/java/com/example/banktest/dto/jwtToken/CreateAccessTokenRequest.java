package com.example.banktest.dto.jwtToken;


import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CreateAccessTokenRequest {

    private String refreshToken;
}
