package com.user_messaging_system.api_gateway.api.input;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Validated
public record UserMessageGetInput (
        @NotBlank(message = "Sender id is mandatory")
        String senderId,
        @NotBlank()
        String receiverId
){ }
