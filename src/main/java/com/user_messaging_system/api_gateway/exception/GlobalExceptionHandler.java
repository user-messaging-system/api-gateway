package com.user_messaging_system.api_gateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        // Hata yanıtını burada ele al
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");

        // Hata yanıtını decode et
        String errorMessage = decodeError(ex);
        byte[] bytes = errorMessage.getBytes();
        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }

    private String decodeError(Throwable throwable) {
        if (throwable instanceof WebClientResponseException) {
            WebClientResponseException webClientResponseException = (WebClientResponseException) throwable;
            String responseBody = webClientResponseException.getResponseBodyAsString();
            ErrorResponse errorResponse = parseErrorResponse(responseBody);
            return "{\"message\": \"" + errorResponse.getBody() + "\", \"code\": \"" + errorResponse.getStatusCode() + "\"}";
        }
        return "{\"message\": \"Unknown error occurred\", \"code\": \"UNKNOWN_ERROR\"}";
    }

    private ErrorResponse parseErrorResponse(String responseBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(responseBody, ErrorResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error decoding error response");
        }
    }
}
