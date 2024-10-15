package com.user_messaging_system.api_gateway.api;

import com.user_messaging_system.api_gateway.api.input.UserMessageGetInput;
import com.user_messaging_system.api_gateway.api.output.UserMessageGetOutput;
import com.user_messaging_system.api_gateway.client.MessageServiceClient;
import com.user_messaging_system.api_gateway.client.UserServiceClient;
import com.user_messaging_system.api_gateway.dto.MessageDTO;
import com.user_messaging_system.api_gateway.dto.UserDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/api/user-message")
public class UserMessageController {
    private final UserServiceClient userServiceClient;
    private final MessageServiceClient messageServiceClient;

    public UserMessageController(UserServiceClient userServiceClient, MessageServiceClient messageServiceClient) {
        this.userServiceClient = userServiceClient;
        this.messageServiceClient = messageServiceClient;
    }

    @GetMapping("/users/{senderId}/messages/{receiverId}")
    public Mono<UserMessageGetOutput> getUserMessages(@ModelAttribute @Valid UserMessageGetInput userMessageGetInput) {
        Mono<UserDTO> senderMono = userServiceClient.getUserById(userMessageGetInput.senderId());
        Mono<UserDTO> receiverMono = userServiceClient.getUserById(userMessageGetInput.receiverId());
        Flux<MessageDTO> messageFlux = messageServiceClient.getMessagesBetweenUsers(
                userMessageGetInput.senderId(),
                userMessageGetInput.receiverId()
        );

        return Mono.zip(senderMono, receiverMono, messageFlux.collectList())
                .map(tuple -> new UserMessageGetOutput(tuple.getT1(), tuple.getT2(), tuple.getT3()));
    }
}
