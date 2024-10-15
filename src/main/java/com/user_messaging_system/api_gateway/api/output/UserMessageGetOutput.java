package com.user_messaging_system.api_gateway.api.output;

import com.user_messaging_system.api_gateway.dto.MessageDTO;
import com.user_messaging_system.api_gateway.dto.UserDTO;

import java.util.List;

public record UserMessageGetOutput(
        UserDTO senderUser,
        UserDTO receiverUser,
        List<MessageDTO> messageDTOList
) { }
