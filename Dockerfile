FROM openjdk:17.0.2-jdk-slim-buster
WORKDIR /app
COPY target/staff-0.0.1.jar .
EXPOSE 8080
ENTRYPOINT ["java","-jar","staff-0.0.1.jar"]