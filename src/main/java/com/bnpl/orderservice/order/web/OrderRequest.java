package com.bnpl.orderservice.order.web;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderRequest (

        @NotNull(message = "The property id must be defined.")
        @Positive(message = "The property id must be positive.")
        Long id

){}