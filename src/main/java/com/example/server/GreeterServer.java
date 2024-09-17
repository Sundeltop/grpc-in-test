package com.example.server;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

import static java.util.concurrent.TimeUnit.SECONDS;

@Log4j2
public class GreeterServer {

    private static final int PORT = 50051;

    private Server server;

    private void start() throws IOException {
        server = Grpc.newServerBuilderForPort(PORT, InsecureServerCredentials.create())
                .addService(new GreeterServiceImpl())
                .build()
                .start();

        log.info("Server started, listening on {}", PORT);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("Shutting down gRPC server since JVM is shutting down");
            try {
                this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("Server shut down");
        }));
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, SECONDS);
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final GreeterServer server = new GreeterServer();

        server.start();
        server.blockUntilShutdown();
    }
}
