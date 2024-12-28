FROM ubuntu:latest
LABEL authors="Mainak Kumar"

# Use the official OpenJDK base image with Java 23
FROM eclipse-temurin:21-jdk-jammy as build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper and pom.xml file to the container
COPY . /app/

# Install Maven and build the application
RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests

# Use a smaller base image for runtime
FROM openjdk:21-ea-2-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=build /app/target/authentication-0.0.1-SNAPSHOT.jar /app/authentication.jar

# Expose the port on which the application will run
EXPOSE 8083 5005

# Command to run the Spring Boot application
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "/app/authentication.jar"]
