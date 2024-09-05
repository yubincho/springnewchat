package com.example.banktest.dto.jwtToken;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class CreateTokenResponse {

    private String accessToken;
}
