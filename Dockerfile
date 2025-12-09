# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies (this layer will be cached if pom.xml doesn't change)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM amazoncorretto:17-alpine-jdk
WORKDIR /app

# Copy the built JAR file from build stage
COPY --from=build /app/target/spring-boot-aws-exe.jar app.jar

# Expose the port your application runs on
EXPOSE 5000

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

