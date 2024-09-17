package com.example.client;

import com.example.GreeterServiceGrpc;
import com.example.HelloRequest;
import com.example.HelloResponse;
import com.google.common.collect.ImmutableList;
import io.grpc.ManagedChannel;

import java.util.List;

public class GreeterClient {

    private final GreeterServiceGrpc.GreeterServiceBlockingStub blockingStub;

    public GreeterClient(ManagedChannel channel) {
        blockingStub = GreeterServiceGrpc.newBlockingStub(channel);
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
}
