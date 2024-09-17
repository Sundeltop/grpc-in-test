package com.example.client;

import com.example.GreeterServiceGrpc;
import com.example.HelloRequest;
import com.example.HelloResponse;
import com.google.common.collect.ImmutableList;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import lombok.SneakyThrows;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;
import static java.time.Duration.ofSeconds;
import static java.util.concurrent.TimeUnit.SECONDS;

public class GreeterClient {

    private final GreeterServiceGrpc.GreeterServiceBlockingStub blockingStub;
    private final GreeterServiceGrpc.GreeterServiceStub stub;

    public GreeterClient(ManagedChannel channel) {
        blockingStub = GreeterServiceGrpc.newBlockingStub(channel);
        stub = GreeterServiceGrpc.newStub(channel);
    }

    public String greetUser(String name) {
        final HelloRequest request = HelloRequest.newBuilder()
                .setName(name)
                .build();

        return blockingStub.sayHelloToUser(request).getMessage();
    }

    public List<String> multipleGreetUser(String name) {
        final HelloRequest request = HelloRequest.newBuilder()
                .setName(name)
                .build();

        return ImmutableList.copyOf(blockingStub.sayMultipleHelloToUser(request))
                .stream()
                .map(HelloResponse::getMessage)
                .toList();
    }

    @SneakyThrows
    public List<String> greetUsers(String... users) {
        final ConcurrentLinkedQueue<HelloRequest> requests = Arrays.stream(users)
                .map(user -> HelloRequest.newBuilder()
                        .setName(user)
                        .build())
                .collect(Collectors.toCollection(ConcurrentLinkedQueue::new));

        final Queue<HelloResponse> responses = new ConcurrentLinkedQueue<>();

        final StreamObserver<HelloRequest> observer = stub
                .withDeadlineAfter(5, SECONDS)
                .sayHelloToUsers(new StreamObserver<>() {
                    @Override
                    public void onNext(HelloResponse helloResponse) {
                        responses.add(helloResponse);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onCompleted() {
                    }
                });

        requests.forEach(request -> {
            observer.onNext(request);
            waitFor(ofSeconds(1));
        });
        observer.onCompleted();

        return responses
                .stream()
                .map(HelloResponse::getMessage)
                .toList();
    }

    @SneakyThrows
    private void waitFor(Duration duration) {
        sleep(duration);
    }
}
