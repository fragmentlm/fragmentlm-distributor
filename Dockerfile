# Use the official OpenJDK 21 image as a base
FROM openjdk:21-jdk-slim AS build

# Set the working directory
WORKDIR /app

# Copy the Gradle build files
COPY build.gradle settings.gradle ./
COPY gradlew gradlew
COPY gradle gradle

# Copy the source code
COPY src src

# Build the application
RUN ./gradlew build --no-daemon

# Use a smaller base image for the final image
FROM openjdk:21-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the application port
EXPOSE 50420/tcp

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
