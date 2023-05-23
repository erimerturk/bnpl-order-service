package com.bnpl.orderservice.order.domain;

import java.time.Instant;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

@Table("orders")
public record Order (

        @Id
        Long id,

        Long propertyId,
        String propertyTitle,
        Double propertyPrice,
        OrderStatus status,

        @CreatedDate
        Instant createdDate,

        @LastModifiedDate
        Instant lastModifiedDate,

        @CreatedBy
        String createdBy,

        @LastModifiedBy
        String lastModifiedBy,

        @Version
        int version
){

    public static Order of(Long propertyId,
                           String propertyTitle,
                           Double propertyPrice, OrderStatus status) {
        return new Order(null, propertyId, propertyTitle, propertyPrice, status, null, null, null,null,0);
    }

}
