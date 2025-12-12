# Build stage
FROM public.ecr.aws/docker/library/maven:3.9-eclipse-temurin-17 AS build

# RUN ping -c 3 google.com

RUN apt-get update && apt-get install -y wget csplit ca-certificates

# 2. Download the AWS RDS Combined CA Bundle
RUN wget -O /tmp/rds-combined-ca-bundle.pem https://s3.amazonaws.com/rds-downloads/rds-combined-ca-bundle.pem

# 3. Split the bundle into individual certificate files (prefixed with 'xx')
RUN csplit -sz /tmp/rds-combined-ca-bundle.pem '/-BEGIN CERTIFICATE-/' '{*}'

# 4. Loop through each individual certificate file and import it into cacerts
# *** CRITICAL: VERIFY THE KEYSTORE PATH for your specific JDK base image ***
RUN for CERT in xx*; do \
    echo "Importing $CERT"; \
    keytool -importcert \
    -trustcacerts \
    -keystore $JAVA_HOME/lib/security/cacerts \
    -storepass changeit \
    -noprompt \
    -alias rds-ca-$CERT \
    -file "$CERT"; \
    done

# 5. Clean up temporary files
RUN rm -rf xx* /tmp/rds-combined-ca-bundle.pem

WORKDIR /app

# Copy pom.xml and download dependencies (this layer will be cached if pom.xml doesn't change)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# 1. Download the combined RDS CA bundle
# Note: This URL is maintained by AWS and contains all regional certificates
RUN wget https://s3.amazonaws.com/rds-downloads/rds-combined-ca-bundle.pem -O /tmp/rds-combined-ca-bundle.pem

# 2. Split the bundle into individual certificates (required by keytool)
RUN csplit -sz /tmp/rds-combined-ca-bundle.pem '/-BEGIN CERTIFICATE-/' '{*}'

# 3. Import each certificate into the default Java Keystore
# WARNING: The 'changeit' password is the default password for the cacerts file.
# The path to cacerts may vary based on your base image (e.g., /usr/lib/jvm/java/jre/lib/security/cacerts)
RUN for CERT in xx*; do \
    keytool -import \
    -keystore $JAVA_HOME/lib/security/cacerts \
    -storepass changeit \
    -noprompt \
    -alias rds$CERT \
    -file "$CERT"; \
    done

# 4. Clean up temp files
RUN rm -rf /tmp/rds-ca /tmp/rds-combined-ca-bundle.pem xx*

# Runtime stage
FROM public.ecr.aws/amazoncorretto/amazoncorretto:17
WORKDIR /app

# Copy the built JAR file from build stage
COPY --from=build /app/target/spring-boot-aws-exe.jar app.jar

# Expose the port your application runs on
EXPOSE 5000

# Run the application
# ENTRYPOINT ["java", "-jar", "app.jar"]