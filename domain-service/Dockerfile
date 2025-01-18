FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/domain-service-0.0.1-SNAPSHOT.jar domain-service.jar
ENTRYPOINT ["java", "-jar", "domain-service.jar"]