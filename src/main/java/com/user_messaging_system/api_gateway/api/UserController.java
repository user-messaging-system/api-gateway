package com.user_messaging_system.api_gateway.api;

import com.user_messaging_system.api_gateway.api.output.UserMessageGetOutput;
import com.user_messaging_system.api_gateway.client.MessageServiceClient;
import com.user_messaging_system.api_gateway.client.UserServiceClient;
import com.user_messaging_system.api_gateway.dto.MessageDTO;
import com.user_messaging_system.api_gateway.dto.UserDTO;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;
import static com.user_messaging_system.core_library.common.constant.APIConstant.*;

@RestController
@RequestMapping(USER_MESSAGE_BASE_URL)
public class UserController {
    private final UserServiceClient userServiceClient;
    private final MessageServiceClient messageServiceClient;

    public UserController(
            UserServiceClient userServiceClient,
            MessageServiceClient messageServiceClient
    ) {
        this.userServiceClient = userServiceClient;
        this.messageServiceClient = messageServiceClient;
    }

    @GetMapping("/messages")
    public Mono<UserMessageGetOutput> getUserMessages(
            @RequestHeader("Authorization") String jwtToken,
            @RequestParam String senderId,
            @RequestParam String receiverId
    ) {
        Mono<List<UserDTO>> senderAndReceiverUsers = userServiceClient.getSenderAndReceiverByIds(jwtToken, senderId, receiverId);
        Flux<MessageDTO> messageFlux = messageServiceClient.getMessagesBetweenUsers(jwtToken, senderId, receiverId);
        return senderAndReceiverUsers.flatMap(users -> {
            return Mono.zip(Mono.just(users.getFirst()), Mono.just(users.get(1)), messageFlux.collectList())
                    .map(tuple -> new UserMessageGetOutput(tuple.getT1(), tuple.getT2(), tuple.getT3()));
        });
    }
}
