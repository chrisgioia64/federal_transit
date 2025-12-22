FROM eclipse-temurin:17-jdk-alpine

ADD target/spring-boot-aws-exe.jar app.jar
ADD src/main/resources/* src/main/resources/
ENTRYPOINT ["sh","-c","java -jar /app.jar"]