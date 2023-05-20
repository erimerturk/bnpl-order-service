package com.bnpl.orderservice;

import com.bnpl.orderservice.order.domain.Order;
import com.bnpl.orderservice.order.domain.OrderStatus;
import com.bnpl.orderservice.order.web.OrderRequest;
import com.bnpl.orderservice.property.Property;
import com.bnpl.orderservice.property.PropertyClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class BnplOrderServiceApplicationTests {

    @Container
    static PostgreSQLContainer<?> postgresql = new PostgreSQLContainer<>(DockerImageName.parse("postgres:14.4"));

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private PropertyClient client;

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", BnplOrderServiceApplicationTests::r2dbcUrl);
        registry.add("spring.r2dbc.username", postgresql::getUsername);
        registry.add("spring.r2dbc.password", postgresql::getPassword);
        registry.add("spring.flyway.url", postgresql::getJdbcUrl);
    }

    private static String r2dbcUrl() {
        return String.format("r2dbc:postgresql://%s:%s/%s", postgresql.getHost(),
                postgresql.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT), postgresql.getDatabaseName());
    }

    @Test
    void whenGetOrdersThenReturn() {
        Long id = 3324l;
        Property property = new Property(id, "Title", "Author", 9.90);
        given(client.getById(id)).willReturn(Mono.just(property));
        OrderRequest orderRequest = new OrderRequest(id);
        Order expectedOrder = webTestClient.post().uri("/orders")
                .bodyValue(orderRequest)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Order.class).returnResult().getResponseBody();
        assertThat(expectedOrder).isNotNull();

        webTestClient.get().uri("/orders")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(Order.class).value(orders -> {
                    assertThat(orders.stream().filter(order -> order.propertyId().equals(id)).findAny()).isNotEmpty();
                });
    }

    @Test
    void whenPostRequestAndPropertyExistsThenOrderAccepted() {
        Long id = 3324324l;
        Property property = new Property(id, "Title", "Author", 9.90);
        given(client.getById(id)).willReturn(Mono.just(property));
        OrderRequest orderRequest = new OrderRequest(id);

        Order createdOrder = webTestClient.post().uri("/orders")
                .bodyValue(orderRequest)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Order.class).returnResult().getResponseBody();

        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.propertyId()).isEqualTo(orderRequest.id());
        assertThat(createdOrder.propertyTitle()).isEqualTo(property.title() + " - " + property.seller());
        assertThat(createdOrder.propertyPrice()).isEqualTo(property.price());
        assertThat(createdOrder.status()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    void whenPostRequestAndPropertyNotExistsThenOrderRejected() {
        Long id = 3324324l;
        given(client.getById(id)).willReturn(Mono.empty());
        OrderRequest orderRequest = new OrderRequest(id);

        Order createdOrder = webTestClient.post().uri("/orders")
                .bodyValue(orderRequest)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Order.class).returnResult().getResponseBody();

        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.propertyId()).isEqualTo(orderRequest.id());
        assertThat(createdOrder.status()).isEqualTo(OrderStatus.REJECTED);
    }

}
