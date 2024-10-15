package com.user_messaging_system.api_gateway.client;

import com.user_messaging_system.api_gateway.dto.UserDTO;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

public interface UserServiceClient {
    Mono<UserDTO> getUserById(@PathVariable String userId);
}
