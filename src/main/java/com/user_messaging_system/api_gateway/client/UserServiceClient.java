package com.user_messaging_system.api_gateway.client;

import com.user_messaging_system.api_gateway.dto.UserDTO;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserServiceClient {
    Mono<List<UserDTO>> getSenderAndReceiverByIds(String jwtToken, String senderId, String receiverId);
}
