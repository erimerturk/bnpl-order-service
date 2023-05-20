package com.bnpl.orderservice.property;

public record Property(
        Long id,
        String title,
        String seller,
        Double price
){}
