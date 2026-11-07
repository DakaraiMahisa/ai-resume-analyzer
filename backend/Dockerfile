# Stage 1: Build the Spring Boot application
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace

# Resolve dependencies separately.
# This layer is reused when only Java source files change.
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copy application source after dependency resolution.
COPY src ./src

RUN mvn -B clean package -DskipTests


# Stage 2: Run the application
FROM eclipse-temurin:21-jre

WORKDIR /app

# Create a dedicated non-root user
RUN groupadd --system app && \
    useradd --system --gid app --home-dir /app --no-create-home app

# Copy the executable JAR
COPY --from=build --chown=app:app \
    /workspace/target/backend-0.0.1-SNAPSHOT.jar \
    /app/app.jar

USER app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]