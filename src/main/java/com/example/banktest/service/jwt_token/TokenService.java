//package com.example.banktest.service.jwt_token;
//
//import com.example.banktest.config.auth.LoginUser;
//import com.example.banktest.config.jwt.JwtProcess;
//import com.example.banktest.domain.jwt.RefreshToken;
//import com.example.banktest.domain.jwt.RefreshTokenRepository;
//import com.example.banktest.domain.user.User;
//import com.example.banktest.service.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//
//@RequiredArgsConstructor
//@Service
//public class TokenService {
//
//    private final JwtProcess jwtProcess;
//    private final RefreshTokenRepository refreshTokenRepository; // 추가
//    private final RefreshTokenService refreshTokenService;
//    private final UserService userService;
//
//    public String createNewAccessToken(String refreshToken) {
//        try {
//            // Refresh Token 검증
//            LoginUser loginUser = jwtProcess.verify(refreshToken);
//            if (loginUser == null) {
//                throw new IllegalArgumentException("Invalid refresh token");
//            }
//
//            // Refresh Token이 저장소에 있는지 확인
//            RefreshToken storedToken = refreshTokenService.findByRefreshToken(refreshToken);
//            if (storedToken == null) {
//                throw new IllegalArgumentException("Refresh token not found in the repository");
//            }
//
//            Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
//            User user = userService.findById(userId);
//
//            // loginUser의 User 객체를 조회된 User로 업데이트
//            if (!user.equals(loginUser.getUser())) {
//                loginUser = new LoginUser(user);
//            }
//
//            // 새로운 Access Token 생성
//            String newAccessToken = jwtProcess.create(loginUser);
//
//            // 새로운 Refresh Token 생성
//            String newRefreshToken = jwtProcess.makeRefreshToken(loginUser);
//
//            // Refresh Token 저장소에 업데이트
//            storedToken.update(newRefreshToken);
//            refreshTokenRepository.save(storedToken);
//
//            return newRefreshToken;
//        } catch (Exception e) {
//            // 토큰 검증 실패 처리
//            throw new IllegalArgumentException("Invalid refresh token", e);
//        }
//    }
//
//}
