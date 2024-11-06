package com.user_messaging_system.api_gateway.api.response;

import java.time.LocalDateTime;

public record ErrorResponse (
        LocalDateTime timestamp,
        String status,
        String message,
        String path
){}
