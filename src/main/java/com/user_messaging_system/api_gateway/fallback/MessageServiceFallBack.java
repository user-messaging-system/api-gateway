package com.user_messaging_system.api_gateway.fallback;

import com.user_messaging_system.api_gateway.client.MessageServiceClient;
import com.user_messaging_system.api_gateway.dto.MessageDTO;
import reactor.core.publisher.Flux;

public class MessageServiceFallBack implements MessageServiceClient {
    @Override
    public Flux<MessageDTO> getMessagesBetweenUsers(String senderId, String receiverId) {
        return null;
    }
}
