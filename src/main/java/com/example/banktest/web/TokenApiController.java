//package com.example.banktest.web;
//
//import com.example.banktest.dto.jwtToken.CreateAccessTokenRequest;
//import com.example.banktest.dto.jwtToken.CreateTokenResponse;
//import com.example.banktest.service.jwt_token.TokenService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//
//@RequiredArgsConstructor
//@RestController
//public class TokenApiController {
//
//    private final TokenService tokenService;
//
//
//    @PostMapping("/api/token")
//    public ResponseEntity<CreateTokenResponse> createNewRefreshToken(@RequestBody CreateAccessTokenRequest request) {
//        String newRefreshToken = tokenService.createNewAccessToken(request.getRefreshToken());
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateTokenResponse(newRefreshToken));
//    }
//}
