package com.challenge.seguros.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${poliza.service.url}")
    private String polizaServiceUrl;

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient polizaWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(polizaServiceUrl)
                .build();
    }
}