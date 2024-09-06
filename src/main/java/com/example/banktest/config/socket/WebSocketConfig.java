package com.example.banktest.config.socket;

import com.example.banktest.config.auth.LoginUser;
import com.example.banktest.config.jwt.JwtVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.example.banktest.config.jwt.JwtProcess;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtProcess jwtProcess;

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

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String token = accessor.getFirstNativeHeader("Authorization");
                    if (token != null && token.startsWith(JwtVO.TOKEN_PREFIX)) {
                        token = token.substring(JwtVO.TOKEN_PREFIX.length());
                        try {
                            LoginUser loginUser = jwtProcess.verify(token);
                            accessor.setUser((Principal) loginUser);
                        } catch (Exception e) {
                            throw new MessageDeliveryException("Invalid token");
                        }
                    }
                }
                return message;
            }
        });
    }

}
