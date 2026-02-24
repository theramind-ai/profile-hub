## Build stage
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn -q -e -B dependency:go-offline

COPY src ./src

RUN mvn -q -e -B clean package -DskipTests

## Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

ENV JAVA_OPTS=""

COPY --from=builder /app/target/profile-hub-backend-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

