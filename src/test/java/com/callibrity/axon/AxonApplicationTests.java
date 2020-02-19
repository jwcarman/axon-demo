package com.callibrity.axon;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@SpringBootTest
class AxonApplicationTests {

    private static final int GRPC_PORT = 8124;
    private static final int HTTP_PORT = 8024;

    @Container
    public static final GenericContainer<?> axonServer = new GenericContainer<>("axoniq/axonserver")
            .withExposedPorts(HTTP_PORT, GRPC_PORT)
            .waitingFor(Wait.forHttp("/actuator/info").forPort(HTTP_PORT))
            .withStartupTimeout(Duration.of(60L, ChronoUnit.SECONDS));

    static {
        axonServer.start();
        System.setProperty("axon.axonserver.servers", String.format("localhost:%d", axonServer.getMappedPort(GRPC_PORT)));
    }

    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private QueryGateway queryGateway;

    @Test
    void contextLoads() {
    }

}
