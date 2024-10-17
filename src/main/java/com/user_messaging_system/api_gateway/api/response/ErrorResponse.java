package com.user_messaging_system.api_gateway.api.response;

public record ErrorResponse (
        String message,
        String code
){}
