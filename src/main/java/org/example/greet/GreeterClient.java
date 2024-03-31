package org.example.greet;

import greet.Greet;
import greet.GreeterGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;

public class GreeterClient {
    private final ManagedChannel channel;
    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    public GreeterClient(String host, int port) {
        // gRPC 채널을 초기화합니다. 서버의 호스트 이름과 포트를 사용합니다.
        this(ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build());
    }

    // ManagedChannel 생성자를 이용하여 GreeterClient 인스턴스를 초기화합니다.
    GreeterClient(ManagedChannel channel) {
        this.channel = channel;
        // 채널을 사용하여 스텁을 생성합니다. 스텁은 서비스 메소드를 원격 호출하는 데 사용됩니다.
        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        // gRPC 채널을 안전하게 종료합니다.
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void greet(String name) {
        // HelloRequest를 생성하여 서버에 전달합니다.
        Greet.HelloRequest request = Greet.HelloRequest.newBuilder().setName(name).build();
        Greet.HelloReply response;
        try {
            // 서버의 SayHello 메소드를 호출하여 응답을 받습니다.
            response = blockingStub.sayHello(request);
            System.out.println("Greeting: " + response.getMessage());
        } catch (StatusRuntimeException e) {
            System.err.println("RPC failed: " + e.getStatus());
            return;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // GreeterClient를 초기화하고 서버에 연결합니다.
        GreeterClient client = new GreeterClient("localhost", 50051);
        try {
            // 사용자 이름을 전달하여 greet 메소드를 호출합니다.
            client.greet("world");
        } finally {
            // 클라이언트 작업이 완료되면 채널을 종료합니다.
            client.shutdown();
        }
    }
}