# Этап 1: Сборка проекта
FROM openjdk:17.0.2-jdk-slim-buster as builder
WORKDIR /opt/app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY ./src ./src
RUN ./mvnw clean install

# Этап 2: Создание финального образа
FROM openjdk:17.0.2-jdk-slim-buster
WORKDIR /opt/app
COPY --from=builder /opt/app/target/staff-0.0.1.jar /opt/app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]