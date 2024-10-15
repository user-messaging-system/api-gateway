package com.user_messaging_system.api_gateway.dto;

public record UserDTO(
        String id,
        String name,
        String lastName,
        String email
) { }