package com.example.server;

import com.example.GreeterServiceGrpc;
import com.example.HelloRequest;
import com.example.HelloResponse;
import io.grpc.stub.StreamObserver;
import lombok.SneakyThrows;

import java.time.Duration;

import static java.lang.Thread.sleep;
import static java.time.Duration.ofSeconds;

public class GreeterServiceImpl extends GreeterServiceGrpc.GreeterServiceImplBase {

    @Override
    public void sayHelloToUser(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        final HelloResponse response = buildHelloResponse("Hello %s".formatted(request.getName()));

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void sayMultipleHelloToUser(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        final HelloResponse firstHello = buildHelloResponse("Hello %s".formatted(request.getName()));

        responseObserver.onNext(firstHello);

        waitFor(ofSeconds(2));

        final HelloResponse secondHello = buildHelloResponse("Hello again %s".formatted(request.getName()));

        responseObserver.onNext(secondHello);
        responseObserver.onCompleted();
    }

    private HelloResponse buildHelloResponse(String message) {
        return HelloResponse.newBuilder()
                .setMessage(message)
                .build();
    }

    @SneakyThrows
    private void waitFor(Duration duration) {
        sleep(duration);
    }
}
