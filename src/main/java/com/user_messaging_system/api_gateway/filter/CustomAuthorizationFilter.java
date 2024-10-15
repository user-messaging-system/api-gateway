package com.user_messaging_system.api_gateway.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user_messaging_system.api_gateway.service.JWTService;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public class CustomAuthorizationFilter implements WebFilter {
    private final JWTService jwtService;
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/v1/api/auth/login",
            "/v1/api/users/register"
            // Add more paths here as needed
    );

    public CustomAuthorizationFilter(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        try{
            if(isExcludedPath(exchange.getRequest().getPath().value())){
                return chain.filter(exchange);
            }

            jwtService.validateToken(exchange.getRequest());
            String token = jwtService.extractToken(exchange.getRequest());
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            jwtService.extractEmail(token),
                            null,
                            jwtService.extractRoles(token)
                    );
            return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authenticationToken));
        }catch (JWTVerificationException jwtVerificationException){
            return handleUnAuthorizedError(exchange);
        }
    }

    private Mono<Void> handleUnAuthorizedError(ServerWebExchange exchange){
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> responseData = Map.of("message", "Invalid token");
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] data;
        try {
            data = objectMapper.writeValueAsBytes(responseData);
        } catch (Exception e) {
            data = new byte[0];
        }
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(data);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    private boolean isExcludedPath(String path) {
        return EXCLUDED_PATHS.contains(path);
    }
}