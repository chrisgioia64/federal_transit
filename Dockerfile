# =============================================================================
# Stage 1: The Build Stage
# =============================================================================
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# =============================================================================
# Stage 2: The Runtime Stage
# =============================================================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 1. Copy the JAR (The Application)
COPY --from=builder /build/target/spring-boot-aws-exe.jar application.jar

# 2. Copy the resources directory (The SQL files)
# We copy explicitly from the builder stage so we don't depend on local files
COPY --from=builder /build/src/main/resources ./resources

EXPOSE 5000

ENTRYPOINT ["java", "-jar", "application.jar"]