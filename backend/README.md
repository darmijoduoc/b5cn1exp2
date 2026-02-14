# EXP2 - Backend Spring Boot

Aplicación Spring Boot con Oracle Database y autenticación de usuarios.

## 🚀 Despliegue con Docker

### Prerrequisitos
- Docker instalado
- Wallet de Oracle en `.credentials/Wallet_GIBI3XSETA997Y7I/`

### Despliegue Automático (Recomendado)

```bash
# Opción 1: Usando Docker Compose (más simple)
docker-compose up -d

# Ver logs
docker-compose logs -f

# Detener
docker-compose down

# Reconstruir y reiniciar
docker-compose up -d --build
```

```bash
# Opción 2: Usando script de despliegue
./deploy.sh
```

Este script automáticamente:
- Detiene y elimina el contenedor anterior
- Construye la imagen Docker
- Inicia el nuevo contenedor
- Muestra los logs iniciales

### Comandos Manuales

```bash
# 1. Construir imagen
docker build -t exp2-backend:latest .

# 2. Ejecutar contenedor
docker run -d \
  --name exp2-backend \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:oracle:thin:@gibi3xseta997y7i_tp \
  -e SPRING_DATASOURCE_USERNAME=ADMIN \
  -e SPRING_DATASOURCE_PASSWORD=aWxpYqvej@bUin3P!tbP \
  exp2-backend:latest

# 3. Ver logs
docker logs -f exp2-backend

# 4. Detener y eliminar
docker stop exp2-backend && docker rm exp2-backend

# 5. Reconstruir todo (desarrollo)
docker stop exp2-backend && docker rm exp2-backend && docker build -t exp2-backend:latest . && docker run -d --name exp2-backend -p 8080:8080 -e SPRING_DATASOURCE_URL=jdbc:oracle:thin:@gibi3xseta997y7i_tp -e SPRING_DATASOURCE_USERNAME=ADMIN -e SPRING_DATASOURCE_PASSWORD=aWxpYqvej@bUin3P!tbP exp2-backend:latest
```

### Usando archivo .env (Recomendado)

```bash
# Crear archivo .env basado en .env.example
cp .env.example .env

# Ejecutar con archivo .env
docker run -d \
  --name exp2-backend \
  -p 8080:8080 \
  --env-file .env \
  exp2-backend:latest
```

## 🧪 Verificar Aplicación

```bash
# Health check
curl http://localhost:8080/authenticate

# Ver logs en tiempo real
docker logs -f exp2-backend

# Inspeccionar contenedor
docker exec -it exp2-backend sh
```

## 🛠️ Comandos Útiles

```bash
# Ver imágenes Docker
docker images

# Eliminar imagen
docker rmi exp2-backend:latest

# Ver contenedores en ejecución
docker ps

# Ver todos los contenedores
docker ps -a

# Limpiar recursos Docker
docker system prune -a
```

## 📝 Endpoints Principales

- `POST /authenticate` - Autenticar usuario
- `POST /forgot_password` - Recuperar contraseña

## 🔧 Desarrollo Local

```bash
# Ejecutar con Maven
./mvnw spring-boot:run

# Compilar
./mvnw clean package
```

