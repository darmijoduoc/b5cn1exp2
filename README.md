
## Create network
```
cd /home/rmji/Projects/duoc/b5/cn1/exp1 && docker network create app-network 2>/dev/null || echo "Red ya existe"
```

## Build and run backend service
```
cd backend && docker build -t cn1_exp1_backend:latest . &&
docker run -d --name backend --network app-network -p 8080:8080 cn1_exp1_backend:latest
```

## Ver logs backend service
```
docker logs -f backend
```

## Stop and remove backend service
```
docker stop backend && docker rm backend
```

## Build and run BFF service
```
cd bff && docker build -t cn1_exp1_bff:latest . &&
docker run -d --name bff --network app-network -p 8081:8081 cn1_exp1_bff:latest
```

## Ver logs BFF service
```
docker logs -f bff
```

## Stop and remove BFF service
```
docker stop bff && docker rm bff
```

## Run images to AWS ECR
```
docker run -d --name backend --network app-network -p 8080:8080 dparmijog/cn1_exp1_backend:latest
docker run -d --name bff --network app-network -p 8081:8081 dparmijog/cn1_exp1_bff:latest
```

---

# EXPERIENCIA 2: Sistema de Monitoreo con RabbitMQ

## 1. Construir imágenes locales
```bash
# Construir todas las imágenes
docker compose build

# O construir individualmente
cd producer-gps && docker build -t exp2-producer-gps:latest .
cd producer-schedule && docker build -t exp2-producer-schedule:latest .
cd consumer-location && docker build -t exp2-consumer-location:latest .
cd consumer-schedule && docker build -t exp2-consumer-schedule:latest .
```

## 2. Taggear imágenes para Docker Hub
```bash
docker tag exp2-producer-gps:latest dparmijog/cn1_exp2_producer_gps:latest
docker tag exp2-producer-schedule:latest dparmijog/cn1_exp2_producer_schedule:latest
docker tag exp2-consumer-location:latest dparmijog/cn1_exp2_consumer_location:latest
docker tag exp2-consumer-schedule:latest dparmijog/cn1_exp2_consumer_schedule:latest
```

## 3. Push a Docker Hub
```bash
# Login a Docker Hub (una sola vez)
docker login

# Push de todas las imágenes
docker push dparmijog/cn1_exp2_producer_gps:latest
docker push dparmijog/cn1_exp2_producer_schedule:latest
docker push dparmijog/cn1_exp2_consumer_location:latest
docker push dparmijog/cn1_exp2_consumer_schedule:latest
```

## 4. Actualizar imágenes (después de cambios)
```bash
# Reconstruir y re-taggear
docker compose build
docker tag exp2-producer-gps:latest dparmijog/cn1_exp2_producer_gps:latest
docker tag exp2-producer-schedule:latest dparmijog/cn1_exp2_producer_schedule:latest
docker tag exp2-consumer-location:latest dparmijog/cn1_exp2_consumer_location:latest
docker tag exp2-consumer-schedule:latest dparmijog/cn1_exp2_consumer_schedule:latest

# Push actualizado
docker push dparmijog/cn1_exp2_producer_gps:latest
docker push dparmijog/cn1_exp2_producer_schedule:latest
docker push dparmijog/cn1_exp2_consumer_location:latest
docker push dparmijog/cn1_exp2_consumer_schedule:latest
```

## 5. Despliegue en AWS
```bash
# Usar docker-compose-aws.yml que apunta a Docker Hub
docker compose -f docker-compose.yml up -d

# Ver logs
docker compose -f docker-compose.yml logs -f

# Detener servicios
docker compose -f docker-compose.yml down
```

## 6. Comandos útiles
```bash
# Ver todas las imágenes taggeadas
docker images | grep dparmijog/cn1_exp2

# Prueba rápida local
./test.py

# Ver logs de un servicio específico
docker logs -f producer-gps
docker logs -f consumer-location

# Reiniciar un servicio
docker compose restart consumer-location

# Limpiar todo
docker compose down -v
```

# Ver archivos
docker exec consumer-schedule ls -lh /app/data/schedules/
docker exec consumer-schedule cat /app/data/schedules/schedule_20260214_200838_79d885d3.json | tail -30
