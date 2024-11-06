package com.user_messaging_system.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.user_messaging_system.core_library"})
@EnableDiscoveryClient
@EnableFeignClients
public class ApiGatewayApplication{
	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}
}