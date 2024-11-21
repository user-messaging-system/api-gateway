package com.user_messaging_system.api_gateway.client;

import com.user_messaging_system.api_gateway.dto.MessageDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MessageServiceClient {
     Flux<MessageDTO> getMessagesBetweenUsers(String jwtToken, String senderId, String receiverId);
     Mono<Void> deleteMessageById(String id);
}
