package com.bnpl.orderservice.order.web;

import java.time.Instant;

import com.bnpl.orderservice.order.domain.Order;
import com.bnpl.orderservice.order.domain.OrderStatus;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class OrderJsonTests {

    @Autowired
    private JacksonTester<Order> json;

    @Test
    void testSerialize() throws Exception {
        var order = new Order(394L, 444l, "Property", 9.90,  OrderStatus.ACCEPTED, Instant.now(), Instant.now(), 21);
        var jsonContent = json.write(order);
        assertThat(jsonContent).extractingJsonPathNumberValue("@.id")
                .isEqualTo(order.id().intValue());
        assertThat(jsonContent).extractingJsonPathNumberValue("@.propertyId")
                .isEqualTo(order.propertyId().intValue());
        assertThat(jsonContent).extractingJsonPathStringValue("@.propertyTitle")
                .isEqualTo(order.propertyTitle());
        assertThat(jsonContent).extractingJsonPathNumberValue("@.propertyPrice")
                .isEqualTo(order.propertyPrice());
        assertThat(jsonContent).extractingJsonPathStringValue("@.status")
                .isEqualTo(order.status().toString());
        assertThat(jsonContent).extractingJsonPathStringValue("@.createdDate")
                .isEqualTo(order.createdDate().toString());
        assertThat(jsonContent).extractingJsonPathStringValue("@.lastModifiedDate")
                .isEqualTo(order.lastModifiedDate().toString());
        assertThat(jsonContent).extractingJsonPathNumberValue("@.version")
                .isEqualTo(order.version());
    }

}