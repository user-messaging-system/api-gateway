package com.user_messaging_system.api_gateway.dto;

public record MessageDTO(
    String id,
    String senderId,
    String receiverId,
    String content
) {}
