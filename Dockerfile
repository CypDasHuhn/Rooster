FROM eclipse-temurin:21-jdk-jammy

# Set working directory
WORKDIR /app

# Copy Gradle files first for caching
COPY gradle gradle
COPY gradlew .
COPY settings.gradle.kts .
COPY build.gradle.kts .

# Copy source files
COPY src src

# Build the application
RUN ./gradlew build --no-daemon

# Expose port if your app uses one
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/build/libs/your-application.jar"]