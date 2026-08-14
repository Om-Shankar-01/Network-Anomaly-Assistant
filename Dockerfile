# Stage 1: Build the application using Gradle
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace/app

# Copy gradle wrapper and configuration first to cache dependencies
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Resolve dependencies
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

# Copy all source code (including the React frontend in static)
COPY src src
COPY proto proto
COPY frontend frontend

# Build the fat jar
RUN ./gradlew bootJar --no-daemon

# Stage 2: Minimal runtime image
FROM eclipse-temurin:17-jre
WORKDIR /app

# Extract the compiled jar from the build stage
COPY --from=build /workspace/app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]