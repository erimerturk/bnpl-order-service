package com.bnpl.orderservice.property;

import com.bnpl.orderservice.config.ClientProperties;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

@TestMethodOrder(MethodOrderer.Random.class)
class PropertyClientTest {

    private MockWebServer mockWebServer;
    private PropertyClient client;

    @BeforeEach
    void setup() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();

        var webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").uri().toString())
                .build();
        this.client = new PropertyClient(webClient, new ClientProperties(mockWebServer.url("/").uri(), 3,3,100));
    }

    @AfterEach
    void clean() throws IOException {
        this.mockWebServer.shutdown();
    }

    @Test
    void whenPropertyExistsThenReturnProperty() {
        var id = 1234567890l;

        var mockResponse = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("""
							{
								"id": %s,
								"title": "Title",
								"seller": "seller",
								"price": 9.90
							}
						""".formatted(id));

        mockWebServer.enqueue(mockResponse);

        Mono<Property> property = client.getById(id);

        StepVerifier.create(property)
                .expectNextMatches(b -> b.id().equals(id))
                .verifyComplete();
    }

    @Test
    void whenPropertyNotExistsThenReturnEmpty() {
        var id = 1234567890l;

        var mockResponse = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(404);

        mockWebServer.enqueue(mockResponse);

        StepVerifier.create(client.getById(id))
                .expectNextCount(0)
                .verifyComplete();
    }

}