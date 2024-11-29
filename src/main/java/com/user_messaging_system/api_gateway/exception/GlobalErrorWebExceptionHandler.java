package com.user_messaging_system.api_gateway.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user_messaging_system.core_library.response.ErrorResponse;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import static com.user_messaging_system.core_library.common.constant.ErrorConstant.NO_ROOT_CAUSE_AVAILABLE;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler implements ErrorWebExceptionHandler {
    private final ObjectMapper objectMapper;

    public GlobalErrorWebExceptionHandler(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        HttpStatus status = determineHttpStatus(ex);

        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        DataBufferFactory dataBufferFactory = response.bufferFactory();
       try {
            String errorResponseBody = objectMapper.writeValueAsString(createErrorResponse(exchange, ex));
            return response.writeWith(Mono.just(
                    dataBufferFactory.wrap(errorResponseBody.getBytes(StandardCharsets.UTF_8))
            ));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    private HttpStatus determineHttpStatus(Throwable ex) {
        if (ex instanceof ResponseStatusException) {
            return HttpStatus.OK;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private ErrorResponse createErrorResponse(ServerWebExchange exchange, Throwable ex){
        return new ErrorResponse.Builder()
                .message(ex.getMessage())
                .errors(List.of(ex.getCause() != null ? ex.getCause().getMessage() : NO_ROOT_CAUSE_AVAILABLE))
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(exchange.getRequest().getURI().getPath())
                .build();
    }
}