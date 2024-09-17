package com.example.server;

import com.example.GreeterServiceGrpc;
import com.example.HelloRequest;
import com.example.HelloResponse;
import io.grpc.stub.StreamObserver;

public class GreeterServiceImpl extends GreeterServiceGrpc.GreeterServiceImplBase {

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        final HelloResponse response = HelloResponse.newBuilder()
                .setMessage("Hello %s".formatted(request.getName()))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
