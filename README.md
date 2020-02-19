# Axon Demo

### Building the Code

We will use [Apache Maven](https://maven.apache.org/) to build the code. The build uses [Testcontainers](https://www.testcontainers.org/) to start up Docker containers during the build for external resources (the Axon Server). The local Docker daemon must be running. Once that is in place, you simply run:

```text
mvn clean install
```

### Setting Up Axon Server

We will use [Axon Server](https://axoniq.io/product-overview/axon-server) to serve as our event store. To start Axon Server locally, issue the following Docker command:

```text
docker run -d --name local-axon-server -p 8024:8024 -p 8124:8124 axoniq/axonserver
``` 

### Running the Demo Application

The demo application is a Spring Boot application, so we can start it using the `spring-boot-maven-plugin`:

```text
mvn spring-boot:run
```

### Creating Some Data

In order to interact with the demo application, we will use its [Swagger UI](http://localhost:8080/swagger-ui.html)