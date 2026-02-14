# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Copy source code and wallet
COPY src ./src
COPY .credentials .credentials

# Build application
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create directory for wallet
RUN mkdir -p /app/.credentials/Wallet_GIBI3XSETA997Y7I

# Copy compiled JAR and wallet
COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/.credentials/Wallet_GIBI3XSETA997Y7I /app/.credentials/Wallet_GIBI3XSETA997Y7I

# Set TNS_ADMIN environment variable
ENV TNS_ADMIN=/app/.credentials/Wallet_GIBI3XSETA997Y7I

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]
