package org.web.gatewayservice.grpc;

import com.web.api.grpc.DomainServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    @Bean
    public ManagedChannel managedChannel() {
        return ManagedChannelBuilder.forAddress("domain-service", 8083)
                .usePlaintext()
                .build();
    }

    @Bean
    public DomainServiceGrpc.DomainServiceBlockingStub domainServiceStub(ManagedChannel channel) {
        return DomainServiceGrpc.newBlockingStub(channel);
    }
}