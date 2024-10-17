package com.user_messaging_system.api_gateway.fallback;

import com.user_messaging_system.api_gateway.api.response.ServiceResponse;
import com.user_messaging_system.api_gateway.client.UserServiceClient;
import com.user_messaging_system.api_gateway.dto.UserDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

import java.util.List;

public class UserServiceFallBack implements UserServiceClient {
    private final WebClient webClient;

    public UserServiceFallBack(WebClient.Builder webClienBuilder){
        this.webClient = webClienBuilder.build();
    }

    @Override
    public Mono<List<UserDTO>> getSenderAndReceiverByIds(String senderId, String receiverId) {
        return webClient.get()
                .uri("http://user-service/v1/api/users/{senderId}/{receiverId}", senderId, receiverId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ServiceResponse<UserDTO>>() {})
                .map(ServiceResponse::data);
    }
}
