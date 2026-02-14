# Producer Schedule - Schedule Updates Publisher

Microservicio productor que recibe actualizaciones de horarios y rutas vía API REST y las publica en RabbitMQ.

## Tecnologías

- Java 21
- Spring Boot 3.4.1
- Spring AMQP (RabbitMQ)
- Maven
- Lombok

## Puerto

`8083`

## Endpoints

### POST /api/producer/schedule/update

Publica una actualización de horario.

**Request Body:**
```json
{
  "updateId": "01HQWXYZ123456789ABCDEFGHI",
  "vehicleId": "BUS-001",
  "routeId": "ROUTE-101",
  "scheduleType": "ARRIVAL",
  "scheduledTime": "2026-02-14T10:45:00",
  "actualTime": "2026-02-14T10:47:00",
  "status": "DELAYED",
  "timestamp": "2026-02-14T10:47:00"
}
```

**Schedule Types:** `ARRIVAL`, `DEPARTURE`, `ROUTE_CHANGE`

**Response:**
```json
{
  "status": "success",
  "message": "Schedule update sent to queue",
  "updateId": "01HQWXYZ123456789ABCDEFGHI",
  "vehicleId": "BUS-001"
}
```

### POST /api/producer/schedule/route-change

Publica un cambio de ruta (mismo formato que /update).

### POST /api/producer/schedule/updates/batch

Publica múltiples actualizaciones en batch.

### GET /api/producer/schedule/health

Health check endpoint.

## Variables de Entorno

- `RABBITMQ_HOST`: Host de RabbitMQ (default: localhost)
- `RABBITMQ_PORT`: Puerto de RabbitMQ (default: 5672)
- `RABBITMQ_USER`: Usuario de RabbitMQ (default: admin)
- `RABBITMQ_PASS`: Contraseña de RabbitMQ (default: admin123)

## Ejecutar Localmente

```bash
./mvnw clean package
./mvnw spring-boot:run

# Probar
curl -X POST http://localhost:8083/api/producer/schedule/update \
  -H "Content-Type: application/json" \
  -d '{
    "updateId": "01HQWXYZ123456789ABCDEFGHI",
    "vehicleId": "BUS-001",
    "routeId": "ROUTE-101",
    "scheduleType": "ARRIVAL",
    "scheduledTime": "2026-02-14T10:45:00",
    "actualTime": "2026-02-14T10:47:00",
    "status": "DELAYED",
    "timestamp": "2026-02-14T10:47:00"
  }'
```

## Docker

```bash
docker build -t producer-schedule:latest .
docker run -d --name producer-schedule -p 8083:8083 -e RABBITMQ_HOST=rabbitmq producer-schedule:latest
```

## Cola RabbitMQ

Este servicio publica mensajes en la cola: `schedule.updates.queue`
