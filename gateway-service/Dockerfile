# Используем базовый образ OpenJDK для Java
FROM openjdk:17-jdk-slim

# Указываем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем JAR-файл из этапа сборки
COPY target/gateway-service-0.0.1-SNAPSHOT.jar gateway-service.jar

# Указываем команду для запуска приложения
ENTRYPOINT ["java", "-jar", "gateway-service.jar"]