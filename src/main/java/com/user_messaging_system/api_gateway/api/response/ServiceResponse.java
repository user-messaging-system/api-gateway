package com.user_messaging_system.api_gateway.api.response;

import java.util.List;

public record ServiceResponse <T>(
        String message,
        String timestamp,
        String status,
        List<T> data
) {}