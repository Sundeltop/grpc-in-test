package com.example.client;

import com.example.GreeterServiceGrpc;
import com.example.HelloRequest;
import io.grpc.ManagedChannel;

public class GreeterClient {

    private final GreeterServiceGrpc.GreeterServiceBlockingStub blockingStub;

    public GreeterClient(ManagedChannel channel) {
        blockingStub = GreeterServiceGrpc.newBlockingStub(channel);
    }

    public String greet(String name) {
        final HelloRequest request = HelloRequest.newBuilder()
                .setName(name)
                .build();

        return blockingStub.sayHello(request).getMessage();
    }
}
