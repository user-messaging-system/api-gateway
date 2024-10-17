package com.user_messaging_system.api_gateway.client;

import com.user_messaging_system.api_gateway.dto.MessageDTO;
import reactor.core.publisher.Flux;


public interface MessageServiceClient {
     Flux<MessageDTO> getMessagesBetweenUsers(String senderId, String receiverId);
}
