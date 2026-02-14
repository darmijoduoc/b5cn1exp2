# Etapa 1: Build
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copiar archivos de Maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Descargar dependencias
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente y wallet
COPY src src
COPY .credentials .credentials

# Compilar aplicación
RUN ./mvnw package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Crear directorio para wallet
RUN mkdir -p /app/.credentials/Wallet_GIBI3XSETA997Y7I

# Copiar JAR compilado y wallet
COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/.credentials/Wallet_GIBI3XSETA997Y7I /app/.credentials/Wallet_GIBI3XSETA997Y7I

# Configurar variable de entorno para Oracle Wallet
ENV TNS_ADMIN=/app/.credentials/Wallet_GIBI3XSETA997Y7I

# Exponer puerto
EXPOSE 8080

# Comando de ejecución
ENTRYPOINT ["java", "-jar", "app.jar"]

