package com.bnpl.orderservice.order.domain;

import com.bnpl.orderservice.property.Property;
import com.bnpl.orderservice.property.PropertyClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final PropertyClient propertyClient;
    private final OrderRepository orderRepository;

    public OrderService(PropertyClient propertyClient, OrderRepository orderRepository) {
        this.propertyClient = propertyClient;
        this.orderRepository = orderRepository;
    }

    public Flux<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Mono<Order> submitOrder(Long id) {
        return propertyClient.getById(id)
                .map(property -> buildAcceptedOrder(property))
                .defaultIfEmpty(buildRejectedOrder(id))
                .flatMap(orderRepository::save);
    }

    public static Order buildAcceptedOrder(Property property) {
        return Order.of(property.id(), property.title() + " - " + property.seller(),
                property.price(), OrderStatus.ACCEPTED);
    }

    public static Order buildRejectedOrder(Long propertyId) {
        return Order.of(propertyId, null, null, OrderStatus.REJECTED);
    }

}