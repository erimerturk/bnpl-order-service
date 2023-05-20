package com.bnpl.orderservice.order.domain;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
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

        @Version
        int version
){

    public static Order of(Long propertyId,
                           String propertyTitle,
                           Double propertyPrice, OrderStatus status) {
        return new Order(null, propertyId, propertyTitle, propertyPrice, status, null, null, 0);
    }

}
