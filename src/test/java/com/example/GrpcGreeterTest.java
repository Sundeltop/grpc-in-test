package com.example;

import com.example.client.GreeterClient;
import com.github.javafaker.Faker;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

public class GrpcGreeterTest {

    private static GreeterClient client;
    private static ManagedChannel channel;

    private final Faker faker = new Faker();

    @BeforeAll
    static void setupClient() {
        channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        client = new GreeterClient(channel);
    }

    @Test
    void verifyUnaryGreetUser() {
        final String expectedUser = faker.name().firstName();

        assertThat(client.greet(expectedUser)).isEqualTo("Hello %s".formatted(expectedUser));
    }

    @AfterAll
    static void shutdownConnection() throws InterruptedException {
        channel
                .shutdownNow()
                .awaitTermination(5, SECONDS);
    }
}
