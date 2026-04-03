####
# Multi-stage build optimized for AWS ECR deployment
# Stage 1: Build the application
####
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy pom.xml and source code
COPY pom.xml ./
RUN mvn dependency:go-offline -B
COPY src ./src

# Build the application using Maven directly (not wrapper)
RUN mvn clean package -DskipTests -B

####
# Stage 2: Runtime image
####
FROM registry.access.redhat.com/ubi9/openjdk-17-runtime:1.21

ENV LANGUAGE='en_US:en'
ENV TZ=UTC

# Create application user for security
USER 185

# Copy built artifacts from builder stage in layers for better caching
COPY --from=builder --chown=185:0 /build/target/quarkus-app/lib/ /deployments/lib/
COPY --from=builder --chown=185:0 /build/target/quarkus-app/*.jar /deployments/
COPY --from=builder --chown=185:0 /build/target/quarkus-app/app/ /deployments/app/
COPY --from=builder --chown=185:0 /build/target/quarkus-app/quarkus/ /deployments/quarkus/

# Expose the port Quarkus listens on
EXPOSE 8080

# Configure JVM options for containerized environments
ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 \
    -Djava.util.logging.manager=org.jboss.logmanager.LogManager \
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:+ExitOnOutOfMemoryError"

ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"

# Health check (optional but recommended for AWS ECS/EKS)
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8080/q/health/live || exit 1

ENTRYPOINT [ "/opt/jboss/container/java/run/run-java.sh" ]