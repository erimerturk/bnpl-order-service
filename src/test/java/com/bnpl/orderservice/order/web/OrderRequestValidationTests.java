package com.bnpl.orderservice.order.web;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class OrderRequestValidationTests {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsCorrectThenValidationSucceeds() {
        var orderRequest = new OrderRequest(3444l);
        Set<ConstraintViolation<OrderRequest>> violations = validator.validate(orderRequest);
        assertThat(violations).isEmpty();
    }


    @Test
    void whenIdIsNotDefinedThenValidationFails() {
        var orderRequest = new OrderRequest( null);
        Set<ConstraintViolation<OrderRequest>> violations = validator.validate(orderRequest);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The property id must be defined.");
    }

    @Test
    void whenIdIsNotPositiveThenValidationFails() {
        var orderRequest = new OrderRequest( 0l);
        Set<ConstraintViolation<OrderRequest>> violations = validator.validate(orderRequest);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The property id must be positive.");
    }

}
