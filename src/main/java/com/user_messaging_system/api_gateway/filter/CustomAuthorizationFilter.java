package com.user_messaging_system.api_gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user_messaging_system.core_library.response.ErrorResponse;
import com.user_messaging_system.core_library.service.JWTService;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

public class CustomAuthorizationFilter implements WebFilter {
    private final JWTService jwtService;
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/v1/api/auth/login",
            "/v1/api/users"
    );

    public CustomAuthorizationFilter(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        try{
            if(isExcludedPath(exchange.getRequest().getPath().value()) && exchange.getRequest().getMethod().equals(HttpMethod.POST)){
                return chain.filter(exchange);
            }

            HttpHeaders headers = exchange.getRequest().getHeaders();
            String token = jwtService.extractCookieHeaderToken(headers.getFirst(HttpHeaders.COOKIE));
            jwtService.validateToken(token);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            jwtService.extractEmail(token),
                            null,
                            jwtService.extractRoles(token)
                    );
            return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authenticationToken));
        }catch (Exception jwtVerificationException){
            return handleUnAuthorizedError(exchange);
        }
    }

    private Mono<Void> handleUnAuthorizedError(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String path = exchange.getRequest().getPath().value();

        ErrorResponse errorResponse = new ErrorResponse.Builder()
                .message("Invalid token")
                .errors(List.of(HttpStatus.UNAUTHORIZED.getReasonPhrase()))
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(path)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        byte[] data;
        try {
            data = objectMapper.writeValueAsBytes(errorResponse);
        } catch (Exception e) {
            data = "{\"message\":\"An unexpected error occurred\"}".getBytes();
        }

        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(data);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }



    private boolean isExcludedPath(String path) {
        return EXCLUDED_PATHS.contains(path);
    }
}