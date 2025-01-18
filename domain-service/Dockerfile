# Этап сборки
FROM maven:3.9.5-eclipse-temurin-17 AS builder
WORKDIR /app

# Копируем корневой POM и модуль
COPY ../pom.xml ../
COPY pom.xml .
COPY src ./src

# Собираем проект
RUN mvn clean install -DskipTests

# Этап запуска
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Копируем скомпилированный JAR из этапа сборки
COPY --from=builder /app/target/domain-service-0.0.1-SNAPSHOT.jar app.jar

# Открываем порт приложения
EXPOSE 8083

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
