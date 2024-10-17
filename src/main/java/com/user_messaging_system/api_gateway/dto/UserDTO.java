package com.user_messaging_system.api_gateway.dto;

import java.util.List;

public record UserDTO(
        String email,
        String name,
        String lastName,
        List<String> roles
) { }