package com.user_messaging_system.api_gateway.fallback;

import com.user_messaging_system.api_gateway.client.UserServiceClient;
import com.user_messaging_system.api_gateway.dto.UserDTO;
import reactor.core.publisher.Mono;

public class UserServiceFallBack implements UserServiceClient {
    @Override
    public Mono<UserDTO> getUserById(String userId) {
        return null;
    }
}
