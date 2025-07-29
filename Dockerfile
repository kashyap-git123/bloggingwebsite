# Use Maven to build the project
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# Copy everything and build using Maven Wrapper
COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Use a lightweight JDK image to run the app
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy the built jar from the previous stage
COPY --from=build /app/target/BloggingWebsite-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
