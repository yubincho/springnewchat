package com.example.banktest.config;


import com.example.banktest.config.jwt.JwtAuthenticationFilter;
import com.example.banktest.config.jwt.JwtAuthorizationFilter;
import com.example.banktest.config.jwt.JwtProcess;
import com.example.banktest.domain.jwt.RedisRefreshTokenRepository;

import com.example.banktest.domain.user.UserEnum;
import com.example.banktest.dto.ResponseDto;
import com.example.banktest.service.jwt_token.RefreshTokenService;
import com.example.banktest.util.CustomResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.logging.Logger;


@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtProcess jwtProcess;
    private final RefreshTokenService refreshTokenService;
    private final RedisRefreshTokenRepository refreshTokenRepository;

    public SecurityConfig(JwtProcess jwtProcess,
                          RefreshTokenService refreshTokenService,
                          RedisRefreshTokenRepository refreshTokenRepository) {
        this.jwtProcess = jwtProcess;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtProcess, refreshTokenService, refreshTokenRepository);
        JwtAuthorizationFilter jwtAuthorizationFilter = new JwtAuthorizationFilter(authenticationManager, jwtProcess, refreshTokenRepository);
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(configurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) -> {
                            CustomResponseUtil.fail(response, "로그인을 해주세요.", HttpStatus.UNAUTHORIZED);
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            CustomResponseUtil.fail(response, "권한이 없습니다.", HttpStatus.FORBIDDEN);
                        })
                )
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/api/join").permitAll()
//                        .requestMatchers("/login").permitAll()
//                        .requestMatchers("/ws/**", "/", "/css/**", "/js/**", "/images/**").permitAll()
//                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/api/**").authenticated()
//                        .anyRequest().authenticated()
//                )
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/login", "/api/join", "/ws/**", "/css/**", "/js/**", "/favicon.ico").permitAll()
                        .requestMatchers("/chat").authenticated()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, JwtAuthorizationFilter.class);
//                .formLogin(formLogin -> formLogin
//                .loginPage("/login")
//                .defaultSuccessUrl("/chat", true));

        return http.build();
    }


    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedOriginPattern("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}