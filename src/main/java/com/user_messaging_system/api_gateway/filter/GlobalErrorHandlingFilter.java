package com.user_messaging_system.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class GlobalErrorHandlingFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange)
            .doOnError(throwable -> handleError(exchange, throwable))
            .onErrorResume(throwable -> handleFallbackResponse(exchange, throwable));
    }

    private Mono<Void> handleFallbackResponse(ServerWebExchange exchange, Throwable throwable) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(throwable);
        }

        if (throwable instanceof WebClientResponseException) {
            WebClientResponseException ex = (WebClientResponseException) throwable;
            HttpStatus status = HttpStatus.resolve(ex.getRawStatusCode());
            status = status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR;

            exchange.getResponse().setStatusCode(status);
            exchange.getResponse().getHeaders().add("Content-Type", "application/json");

            byte[] bytes = ex.getResponseBodyAsByteArray();
            if (bytes.length == 0) {
                String errorResponse = String.format("{\"message\": \"%s\", \"status\": %d}",
                    "An error occurred while processing your request",
                    status.value());
                bytes = errorResponse.getBytes(StandardCharsets.UTF_8);
            }

            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        }

        HttpStatus status = determineHttpStatus(throwable);
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");

        String errorResponse = String.format("{\"message\": \"%s\", \"status\": %d}",
            "An error occurred while processing your request",
            status.value());

        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(errorResponse.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    private HttpStatus determineHttpStatus(Throwable throwable) {
        if (throwable instanceof WebClientResponseException.NotFound) {
            return HttpStatus.NOT_FOUND;
        } else if (throwable instanceof WebClientResponseException.BadRequest) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private void handleError(ServerWebExchange exchange, Throwable throwable) {
        System.err.println("Hata meydana geldi: " + throwable.getMessage());

        exchange.getAttributes().put("error", throwable);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}


