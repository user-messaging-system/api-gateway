package com.user_messaging_system.api_gateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalErrorHandler implements ErrorWebExceptionHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if(response.isCommitted()){
            return Mono.error(ex);
        }

        Map<String, Object> errorAttributes = getErrorAttributes(exchange, ex);
        HttpStatus status = determineHttpStatus(ex);

        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        DataBufferFactory bufferFactory = response.bufferFactory();

        return Mono.fromCallable(() -> {
            try{
                byte[] bytes = objectMapper.writeValueAsBytes(errorAttributes);
                return bufferFactory.wrap(bytes);
            } catch (JsonProcessingException e){
                return bufferFactory.wrap(new byte[0]);
            }
        }).flatMap(dataBuffer -> response.writeWith(Mono.just(dataBuffer)));
    }

    private Map<String, Object> getErrorAttributes(ServerWebExchange exchange, Throwable ex) {
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("timestamp", Instant.now());
        errorAttributes.put("path", exchange.getRequest().getPath().value());
        errorAttributes.put("message", "An unexpected error occurred");
        errorAttributes.put("error", ex.getClass().getSimpleName());
        return errorAttributes;
    }

    private HttpStatus determineHttpStatus(Throwable ex) {
        if (ex instanceof NotFoundException) {
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}