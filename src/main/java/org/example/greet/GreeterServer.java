package org.example.greet;

import greet.Greet;
import greet.GreeterGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class GreeterServer {
    private Server server;

    private void start() throws Exception {
        int port = 50051;
        server = ServerBuilder.forPort(port)
            .addService(new GreeterImpl())
            .build()
            .start();
        System.out.println("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            GreeterServer.this.stop();
            System.err.println("*** server shut down");
        }));
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws Exception {
        final GreeterServer server = new GreeterServer();
        server.start();
        server.blockUntilShutdown();
    }

    static class GreeterImpl extends GreeterGrpc.GreeterImplBase {
        @Override
        public void sayHello(Greet.HelloRequest req, StreamObserver<Greet.HelloReply> responseObserver) {
            Greet.HelloReply reply = Greet.HelloReply.newBuilder().setMessage("Hello, " + req.getName()).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
