package com.bnpl.orderservice.config;

import java.net.URI;


import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bnpl")
public record ClientProperties (

        @NotNull
        URI catalogServiceUri,

        int catalogTimeOut,
        int catalogMaxAttempt,
        int catalogMinBackoff

){ }