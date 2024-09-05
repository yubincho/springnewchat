package com.example.banktest.config.socket;

import org.springframework.context.annotation.Configuration;

import com.example.banktest.config.jwt.JwtProcess;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
//        registry.enableSimpleBroker("/topic");
//        registry.setApplicationDestinationPrefixes("/app");
//        registry.enableSimpleBroker("/subscribe"); // /subscribe/{chatNo}로 주제 구독 가능
//        registry.setApplicationDestinationPrefixes("/publish"); // /publish/message로 메시지 전송 컨트롤러 라우팅 가능
//        A는 1번 채팅방에 접속 -> A가 /subscribe/1을 구독함
//        B와 C는 2번 채팅방에 접속 -> B와 C는 /subscribe/2를 구독
//        C가 메시지를 보냄 -> 현재 같은 채팅방을 구독중인 B에게만 메시지가 전달됨
    }


//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(webSocketAuthInterceptor);
//    }

}
