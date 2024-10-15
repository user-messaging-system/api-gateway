package com.user_messaging_system.api_gateway.api.input;

import jakarta.validation.constraints.NotBlank;

public record UserMessageGetInput (
        @NotBlank
        String senderId,
        @NotBlank
        String receiverId
){ }
