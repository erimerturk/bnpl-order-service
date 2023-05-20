package com.bnpl.orderservice.property;

import java.time.Duration;

import com.bnpl.orderservice.config.ClientProperties;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class PropertyClient {

    private static final String PROPERTY_ROOT_API = "/properties/";
    private final WebClient webClient;
    private final ClientProperties clientProperties;

    public PropertyClient(WebClient webClient, ClientProperties clientProperties) {
        this.webClient = webClient;
        this.clientProperties = clientProperties;
    }

    public Mono<Property> getById(Long id) {
        return webClient
                .get()
                .uri(PROPERTY_ROOT_API + id)
                .retrieve()
                .bodyToMono(Property.class)
                .timeout(Duration.ofSeconds(clientProperties.catalogTimeOut()), Mono.empty())
                .onErrorResume(WebClientResponseException.NotFound.class, exception -> Mono.empty())
                .retryWhen(Retry.backoff(clientProperties.catalogMaxAttempt(), Duration.ofMillis(clientProperties.catalogMinBackoff())))
                .onErrorResume(Exception.class, exception -> Mono.empty());
    }

}