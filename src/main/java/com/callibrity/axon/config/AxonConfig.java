package com.callibrity.axon.config;

import org.axonframework.commandhandling.distributed.AnnotationRoutingStrategy;
import org.axonframework.commandhandling.distributed.RoutingStrategy;
import org.axonframework.commandhandling.distributed.UnresolvedRoutingKeyPolicy;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.Snapshotter;
import org.axonframework.messaging.interceptors.BeanValidationInterceptor;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.ValidatorFactory;

@Configuration
public class AxonConfig {

    @Bean
    CommandLineRunner registerCommandValidation(CommandGateway gateway, ValidatorFactory factory) {
        return (args) -> gateway.registerDispatchInterceptor(new BeanValidationInterceptor<>(factory));
    }

    @Bean
    CommandLineRunner registerQueryValidation(QueryGateway gateway, ValidatorFactory factory) {
        return args -> gateway.registerDispatchInterceptor(new BeanValidationInterceptor<>(factory));
    }

    @Bean
    public RoutingStrategy routingStrategy() {
        return new AnnotationRoutingStrategy(UnresolvedRoutingKeyPolicy.STATIC_KEY);
    }

    @Bean
    public SnapshotTriggerDefinition accountSnapshotTrigger(Snapshotter snapshotter) {
        return new EventCountSnapshotTriggerDefinition(snapshotter, 5);
    }
}
