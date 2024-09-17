package com.example;

import com.example.client.GreeterClient;
import com.github.javafaker.Faker;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

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
        final String user = faker.name().firstName();

        final String expectedGreeting = "Hello %s".formatted(user);

        assertThat(client.greetUser(user)).isEqualTo(expectedGreeting);
    }

    @Test
    void verifyServerStreamingMultipleGreetUser() {
        final String user = faker.name().firstName();

        final List<String> expectedGreetings = List.of(
                "Hello %s".formatted(user),
                "Hello again %s".formatted(user)
        );

        assertThat(client.multipleGreetUser(user)).isEqualTo(expectedGreetings);
    }

    @Test
    void verifyClientStreamingGreetMultipleUsers() {
        final String firstUser = faker.name().firstName();
        final String secondUser = faker.name().firstName();

        final String expectedGreeting = "Hello %s".formatted(
                String.join(",", firstUser, secondUser));

        assertThat(client.joinedGreetUsers(firstUser, secondUser)).isEqualTo(expectedGreeting);
    }

    @Test
    void verifyBidirectionalStreamingGreetUsers() {
        final String firstUser = faker.name().firstName();
        final String secondUser = faker.name().firstName();

        final List<String> expectedGreetings = Stream.of(firstUser, secondUser)
                .map("Hello %s"::formatted)
                .toList();

        assertThat(client.separateGreetUsers(firstUser, secondUser)).isEqualTo(expectedGreetings);
    }

    @AfterAll
    static void shutdownConnection() throws InterruptedException {
        channel
                .shutdownNow()
                .awaitTermination(5, SECONDS);
    }
}
