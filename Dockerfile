# Stage 1: Build with Maven Wrapper
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Stage 2: Run with Java 21
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

COPY --from=build /app/target/BloggingWebsite-0.0.1-SNAPSHOT.jar app.jar

COPY wait-for-db.sh /wait-for-db.sh
RUN chmod +x /wait-for-db.sh




EXPOSE 8080
ENTRYPOINT ["sh", "/wait-for-db.sh"]
#ENTRYPOINT ["java", "-jar", "app.jar"]
