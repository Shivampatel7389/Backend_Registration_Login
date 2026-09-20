# Stage 1: Build application with Maven and JDK 21
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build executable JAR
RUN mvn clean package -DskipTests

# Stage 2: Production-ready lightweight JRE 21 runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the executable jar from build stage
COPY --from=build /app/target/RegLogBackApp-1.0.0.jar app.jar

# Default exposed port (Render injects $PORT)
EXPOSE 8080

# Execute Spring Boot application
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]
