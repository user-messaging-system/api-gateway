package com.user_messaging_system.api_gateway.api.response;

public record UserServiceGetUserResponse(
        String id,
        String email,
        String name,
        String lastName,
        String password
) {}
