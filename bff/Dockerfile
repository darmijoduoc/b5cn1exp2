FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copiar archivos de Maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Descargar dependencias
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente
COPY src src

RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Variables de entorno para configuración
ENV SERVER_PORT=8081
ENV BACKEND_URL=http://backend:8080

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

