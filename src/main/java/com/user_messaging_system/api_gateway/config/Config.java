package com.user_messaging_system.api_gateway.config;

import com.user_messaging_system.api_gateway.filter.GlobalWebErrorHandlerFilter;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class Config {
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .defaultHeader("Content-Type", "application/json")
                .filter((request, next) -> {
                    return next.exchange(request);
                });
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r.path("/v1/api/users/**")
                    .filters(f -> f.filter(new GlobalWebErrorHandlerFilter()))
                    .uri("lb://user-service"))
                .route("message-service", r -> r.path("/message/**")
                        .uri("lb://message-service"))
                .build();
    }
}
