# Stage 1: Build the JAR file
FROM maven:3.8.5-openjdk-17 AS builder

WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
COPY .env /app/.env
RUN mvn dependency:go-offline

COPY src/ ./src/
#RUN mvn clean package -DskipTests=true
RUN mvn clean install -Dmaven.test.skip=true

# Stage 2: Create the final image
FROM openjdk:17-jdk-slim AS prod

WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=builder /app/target/*.jar /app/app.jar

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=production

# Expose the application's port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
