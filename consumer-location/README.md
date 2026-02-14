# Consumer Location - GPS Location Database Consumer

Microservicio consumidor que escucha mensajes de ubicaciones GPS desde RabbitMQ y los almacena en Oracle Database.

## Tecnologías

- Java 21
- Spring Boot 3.4.1
- Spring AMQP (RabbitMQ)
- Spring Data JPA
- Oracle JDBC
- Maven
- Lombok

## Puerto

`8084`

## Funcionalidad

Este servicio:
1. Escucha la cola `gps.locations.queue` de RabbitMQ
2. Recibe mensajes con ubicaciones GPS de vehículos
3. Convierte el mensaje DTO a entidad JPA
4. Persiste los datos en tabla `VEHICLE_LOCATIONS` de Oracle Database

## Modelo de Datos

### Tabla: VEHICLE_LOCATIONS

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | LONG | Primary key (auto-increment) |
| vehicle_id | VARCHAR(50) | ID del vehículo (indexed) |
| latitude | DOUBLE | Latitud GPS |
| longitude | DOUBLE | Longitud GPS |
| timestamp | DATETIME | Timestamp del GPS (indexed) |
| speed | DOUBLE | Velocidad en km/h (opcional) |
| route | VARCHAR(50) | ID de ruta (opcional) |
| created_at | DATETIME | Timestamp de creación del registro |

## Variables de Entorno

### RabbitMQ
- `RABBITMQ_HOST`: Host de RabbitMQ (default: localhost)
- `RABBITMQ_PORT`: Puerto de RabbitMQ (default: 5672)
- `RABBITMQ_USER`: Usuario de RabbitMQ (default: admin)
- `RABBITMQ_PASS`: Contraseña de RabbitMQ (default: admin123)

### Oracle Database
- `SPRING_DATASOURCE_URL`: JDBC URL (default: jdbc:oracle:thin:@gibi3xseta997y7i_tp)
- `SPRING_DATASOURCE_USERNAME`: Usuario de BD (default: ADMIN)
- `SPRING_DATASOURCE_PASSWORD`: Contraseña de BD
- `TNS_ADMIN`: Ruta al wallet de Oracle (default: /app/.credentials/Wallet_GIBI3XSETA997Y7I)

## Ejecutar Localmente

```bash
# Asegurarse de que el wallet de Oracle está en .credentials/
./mvnw clean package
./mvnw spring-boot:run
```

## Docker

```bash
docker build -t consumer-location:latest .
docker run -d \
  --name consumer-location \
  -p 8084:8084 \
  -e RABBITMQ_HOST=rabbitmq \
  -e SPRING_DATASOURCE_PASSWORD=yourpass \
  consumer-location:latest
```

## Cola RabbitMQ

Este servicio consume mensajes de la cola: `gps.locations.queue`

## Verificación

Para verificar que está funcionando:

1. Ver logs del contenedor: `docker logs -f consumer-location`
2. Enviar mensaje desde Producer GPS
3. Consultar tabla en Oracle:
```sql
SELECT * FROM VEHICLE_LOCATIONS ORDER BY created_at DESC;
```
