
# Stage 1: Build the Spring Boot application
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace

# Resolve dependencies separately.
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copy application source.
COPY src ./src

RUN mvn -B clean package -DskipTests


# Stage 2: Run the application
FROM eclipse-temurin:21-jre-noble

WORKDIR /app

ARG APPLICATION_INSIGHTS_AGENT_VERSION=3.7.10

# Apply security updates and download the pinned Azure Monitor agent.
# Verify its SHA-256 checksum before using it.
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y --no-install-recommends curl ca-certificates && \
    curl -fSL --retry 3 \
      "https://github.com/microsoft/ApplicationInsights-Java/releases/download/${APPLICATION_INSIGHTS_AGENT_VERSION}/applicationinsights-agent-${APPLICATION_INSIGHTS_AGENT_VERSION}.jar" \
      -o /app/applicationinsights-agent.jar && \
    echo "93a70c8f5d364c7e777f6c4d1b235dba91aef8448bd3fa94359f1d7f3e2eb0ec  /app/applicationinsights-agent.jar" | sha256sum -c - && \
    apt-get purge -y curl && \
    apt-get autoremove -y && \
    rm -rf /var/lib/apt/lists/*

# Create a dedicated non-root user.
RUN groupadd --system app && \
    useradd --system --gid app --home-dir /app --no-create-home app

COPY --chown=app:app applicationinsights.json /app/applicationinsights.json

# Copy the executable JAR.
COPY --from=build --chown=app:app \
    /workspace/target/backend-0.0.1-SNAPSHOT.jar \
    /app/app.jar


RUN chown app:app /app/applicationinsights-agent.jar

USER app

EXPOSE 8080

# Start the application with Azure Monitor instrumentation.
ENTRYPOINT ["sh", "-c", "if [ -n \"$APPLICATIONINSIGHTS_CONNECTION_STRING\" ]; then exec java -javaagent:/app/applicationinsights-agent.jar -jar /app/app.jar; else echo 'Application Insights agent disabled: no connection string provided'; exec java -jar /app/app.jar; fi"]