package com.user_messaging_system.api_gateway.client.impl;

import com.user_messaging_system.api_gateway.api.response.ServiceResponse;
import com.user_messaging_system.api_gateway.client.UserServiceClient;
import com.user_messaging_system.api_gateway.dto.UserDTO;
import com.user_messaging_system.core_library.service.JWTService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class UserServiceClientImpl implements UserServiceClient {
    private final WebClient webClient;
    private final JWTService jwtService;

    public UserServiceClientImpl(WebClient.Builder webClienBuilder, JWTService jwtService){
        this.webClient = webClienBuilder.build();
        this.jwtService = jwtService;
    }

    @Override
    public Mono<List<UserDTO>> getSenderAndReceiverByIds(String jwtToken, String senderId, String receiverId) {
        return webClient.get()
            .uri("http://user-service/v1/api/users/{senderId}/{receiverId}", senderId, receiverId)
            .header("Authorization", jwtToken)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<ServiceResponse<UserDTO>>() {})
            .map(ServiceResponse::data)
            .onErrorResume(WebClientResponseException.class, ex -> {
                return Mono.error(ex);
            });
    }
}
