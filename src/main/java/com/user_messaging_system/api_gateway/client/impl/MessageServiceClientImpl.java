package com.user_messaging_system.api_gateway.client.impl;

import com.user_messaging_system.api_gateway.client.MessageServiceClient;
import com.user_messaging_system.api_gateway.dto.MessageDTO;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class MessageServiceClientImpl implements MessageServiceClient {
    private final WebClient webClient;

    public MessageServiceClientImpl(WebClient.Builder webClienBuilder){
        this.webClient = webClienBuilder.build();
    }

    @Override
    public Flux<MessageDTO> getMessagesBetweenUsers(String jwtToken, String senderId, String receiverId) {
        return webClient.get()
                .uri("http://message-service/v1/api/messages/conversations/{senderId}/{receiverId}", senderId, receiverId)
                .header("Authorization", jwtToken)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new Exception("Error: " + errorBody)))
                )
                .bodyToFlux(MessageDTO.class);
    }

    @Override
    public Mono<Void> deleteMessageById(String id){
        return null;
    }
}
