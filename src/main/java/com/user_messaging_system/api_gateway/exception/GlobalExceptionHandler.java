package com.user_messaging_system.api_gateway.exception;

import com.user_messaging_system.core_library.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Component
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(
        Exception exception,
        WebRequest request
    ){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse.Builder()
                .message(exception.getMessage())
                .error(exception.getCause() != null ? exception.getCause().getMessage() : "No root cause available")
                .status(HttpStatus.NOT_FOUND.value())
                .path(request.getDescription(false))
                .build()
            );
    }
}
