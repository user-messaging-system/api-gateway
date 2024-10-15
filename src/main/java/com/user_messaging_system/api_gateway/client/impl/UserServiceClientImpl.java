package com.user_messaging_system.api_gateway.client.impl;

import com.user_messaging_system.api_gateway.client.UserServiceClient;
import com.user_messaging_system.api_gateway.dto.UserDTO;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserServiceClientImpl implements UserServiceClient {
    private final WebClient webClient;

    public UserServiceClientImpl(WebClient.Builder webClienBuilder){
        this.webClient = webClienBuilder.build();
    }

    @Override
    public Mono<UserDTO> getUserById(String userId) {
        return webClient.get()
                .uri("http://user-service/v1/api/users/{userId}", userId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new Exception("Error: " + errorBody)))
                )
                .bodyToMono(UserDTO.class);
    }
}
