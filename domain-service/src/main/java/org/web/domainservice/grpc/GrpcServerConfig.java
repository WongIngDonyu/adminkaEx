package org.web.domainservice.grpc;


import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web.domainservice.services.DomainServiceGrpcImpl;

@Configuration
public class GrpcServerConfig {

    @Bean
    public Server grpcServer(DomainServiceGrpcImpl domainServiceGrpcImpl) throws Exception {
        Server server = ServerBuilder.forPort(8083)
                .addService(domainServiceGrpcImpl)
                .build();
        server.start();
        System.out.println("gRPC Server started, listening on port 9090");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gRPC Server...");
            server.shutdown();
            System.out.println("gRPC Server shut down successfully");
        }));

        return server;
    }
}