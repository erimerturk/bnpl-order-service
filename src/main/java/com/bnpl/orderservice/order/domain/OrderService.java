package com.bnpl.orderservice.order.domain;

import com.bnpl.orderservice.order.event.OrderAcceptedMessage;
import com.bnpl.orderservice.order.event.OrderDispatchedMessage;
import com.bnpl.orderservice.property.Property;
import com.bnpl.orderservice.property.PropertyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final PropertyClient propertyClient;
    private final OrderRepository orderRepository;

    private final StreamBridge streamBridge;

    public OrderService(PropertyClient propertyClient, OrderRepository orderRepository, StreamBridge streamBridge) {
        this.propertyClient = propertyClient;
        this.orderRepository = orderRepository;
        this.streamBridge = streamBridge;
    }

    public Flux<Order> getAllOrders(String userId) {
        return orderRepository.findAllByCreatedBy(userId);
    }

    @Transactional
    public Mono<Order> submitOrder(Long id) {
        return propertyClient.getById(id)
                .map(property -> buildAcceptedOrder(property))
                .defaultIfEmpty(buildRejectedOrder(id))
                .flatMap(orderRepository::save)
                .doOnNext(this::publishOrderAcceptedEvent);
    }

    public static Order buildAcceptedOrder(Property property) {
        return Order.of(property.id(), property.title() + " - " + property.seller(),
                property.price(), OrderStatus.ACCEPTED);
    }

    public static Order buildRejectedOrder(Long propertyId) {
        return Order.of(propertyId, null, null, OrderStatus.REJECTED);
    }

    private void publishOrderAcceptedEvent(Order order) {
        if (!order.status().equals(OrderStatus.ACCEPTED)) {
            return;
        }
        var orderAcceptedMessage = new OrderAcceptedMessage(order.id());
        log.info("Sending order accepted event with id: {}", order.id());
        var result = streamBridge.send("acceptOrder-out-0", orderAcceptedMessage);
        log.info("Result of sending data for order with id {}: {}", order.id(), result);
    }

    public Flux<Order> consumeOrderDispatchedEvent(Flux<OrderDispatchedMessage> flux) {
        return flux
                .flatMap(message -> orderRepository.findById(message.orderId()))
                .map(this::buildDispatchedOrder)
                .flatMap(orderRepository::save);
    }

    private Order buildDispatchedOrder(Order existingOrder) {
        return new Order(
                existingOrder.id(),
                existingOrder.propertyId(),
                existingOrder.propertyTitle(),
                existingOrder.propertyPrice(),
                OrderStatus.DISPATCHED,
                existingOrder.createdDate(),
                existingOrder.lastModifiedDate(),
                existingOrder.createdBy(),
                existingOrder.lastModifiedBy(),
                existingOrder.version()
        );
    }

}