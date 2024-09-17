package com.example.server;

import com.example.GreeterServiceGrpc;
import com.example.HelloRequest;
import com.example.HelloResponse;
import io.grpc.stub.StreamObserver;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;
import static java.time.Duration.ofSeconds;

@Log4j2
public class GreeterServiceImpl extends GreeterServiceGrpc.GreeterServiceImplBase {

    @Override
    public void sayHelloToUser(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        final HelloResponse response = buildHelloResponse("Hello %s".formatted(request.getName()));

        log.info("Produce response: {}", response.getMessage());
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void sayMultipleHelloToUser(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        final HelloResponse firstHello = buildHelloResponse("Hello %s".formatted(request.getName()));

        log.info("Produce response: '{}'", firstHello.getMessage());
        responseObserver.onNext(firstHello);

        waitFor(ofSeconds(2));

        final HelloResponse secondHello = buildHelloResponse("Hello again %s".formatted(request.getName()));

        log.info("Produce response: '{}'", secondHello.getMessage());
        responseObserver.onNext(secondHello);

        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<HelloRequest> sayJoinedHelloToUsers(StreamObserver<HelloResponse> responseObserver) {
        return new StreamObserver<>() {
            final Queue<HelloRequest> requests = new ConcurrentLinkedQueue<>();

            @Override
            public void onNext(HelloRequest request) {
                requests.add(request);
            }

            @Override
            public void onError(Throwable throwable) {
                log.error(throwable);
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                final String joinedNames = requests
                        .stream()
                        .map(HelloRequest::getName)
                        .collect(Collectors.joining(", "));

                final HelloResponse response = buildHelloResponse("Hello %s".formatted(joinedNames));

                log.info("Produce response: '{}'", response.getMessage());
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<HelloRequest> saySeparateHelloToUsers(StreamObserver<HelloResponse> responseObserver) {
        return new StreamObserver<>() {

            @Override
            public void onNext(HelloRequest request) {
                final HelloResponse response = buildHelloResponse("Hello %s".formatted(request.getName()));

                log.info("Produce response: '{}'", response.getMessage());
                responseObserver.onNext(response);
            }

            @Override
            public void onError(Throwable throwable) {
                log.error(throwable);
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
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
