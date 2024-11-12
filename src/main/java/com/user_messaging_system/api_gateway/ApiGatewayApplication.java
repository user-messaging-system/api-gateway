package com.user_messaging_system.api_gateway;

import com.user_messaging_system.core_library.service.JWTService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@Import(JWTService.class)
public class ApiGatewayApplication{
	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}
}