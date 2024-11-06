package com.user_messaging_system.api_gateway.api.output;

import java.util.List;

public record UserGetOutput(
        String name,
        String lastName,
        String email,
        List<String> roles
) {}
