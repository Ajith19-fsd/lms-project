# Use Eclipse Temurin JDK 21 as base image
FROM eclipse-temurin:21-jdk-alpine AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies first (for caching)
COPY pom.xml .
RUN mkdir -p src && echo "" > src/.placeholder
RUN ./mvnw dependency:go-offline || true

# Copy the rest of the project
COPY . .

# Package the Spring Boot application
RUN ./mvnw clean package -DskipTests

# Second stage: minimal runtime image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/lmsbackend-1.0.0.jar app.jar

# Expose default Spring Boot port
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java","-jar","app.jar"]