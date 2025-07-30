# ── Build Stage ───────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Install Maven
RUN apk add --no-cache maven

# Copy project metadata and resolve dependencies
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Copy source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests -B

# ── Runtime Stage ─────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy built jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Optional: Create logs directory
RUN mkdir -p logs

# Expose application port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
