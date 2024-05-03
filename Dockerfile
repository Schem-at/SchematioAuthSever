FROM openjdk:21-jdk-slim

WORKDIR /app

COPY build/libs/*.jar /app/server.jar

CMD ["java", "-jar", "server.jar"]