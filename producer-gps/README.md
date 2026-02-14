# Producer GPS - GPS Location Publisher

Microservicio productor que recibe ubicaciones GPS de vehículos vía API REST y las publica en RabbitMQ.

## Tecnologías

- Java 21
- Spring Boot 3.4.1
- Spring AMQP (RabbitMQ)
- Maven
- Lombok

## Puerto

`8082`

## Endpoints

### POST /api/producer/gps/location

Publica una ubicación GPS individual.

**Request Body:**
```json
{
  "vehicleId": "BUS-001",
  "latitude": -33.4489,
  "longitude": -70.6693,
  "timestamp": "2026-02-14T10:30:00",
  "speed": 45.5,
  "route": "ROUTE-101"
}
```

**Response:**
```json
{
  "status": "success",
  "message": "Location sent to queue",
  "vehicleId": "BUS-001",
  "timestamp": "2026-02-14T10:30:00"
}
```

### POST /api/producer/gps/locations/batch

Publica múltiples ubicaciones GPS en batch.

**Request Body:**
```json
[
  {
    "vehicleId": "BUS-001",
    "latitude": -33.4489,
    "longitude": -70.6693,
    "timestamp": "2026-02-14T10:30:00",
    "speed": 45.5,
    "route": "ROUTE-101"
  },
  {
    "vehicleId": "BUS-002",
    "latitude": -33.4500,
    "longitude": -70.6700,
    "timestamp": "2026-02-14T10:31:00",
    "speed": 50.0,
    "route": "ROUTE-102"
  }
]
```

### GET /api/producer/gps/health

Health check endpoint.

## Variables de Entorno

- `RABBITMQ_HOST`: Host de RabbitMQ (default: localhost)
- `RABBITMQ_PORT`: Puerto de RabbitMQ (default: 5672)
- `RABBITMQ_USER`: Usuario de RabbitMQ (default: admin)
- `RABBITMQ_PASS`: Contraseña de RabbitMQ (default: admin123)

## Ejecutar Localmente

```bash
# Compilar
./mvnw clean package

# Ejecutar
./mvnw spring-boot:run

# Probar
curl -X POST http://localhost:8082/api/producer/gps/location \
  -H "Content-Type: application/json" \
  -d '{
    "vehicleId": "BUS-001",
    "latitude": -33.4489,
    "longitude": -70.6693,
    "timestamp": "2026-02-14T10:30:00",
    "speed": 45.5,
    "route": "ROUTE-101"
  }'
```

## Docker

```bash
# Build
docker build -t producer-gps:latest .

# Run
docker run -d \
  --name producer-gps \
  -p 8082:8082 \
  -e RABBITMQ_HOST=rabbitmq \
  producer-gps:latest
```

## Cola RabbitMQ

Este servicio publica mensajes en la cola: `gps.locations.queue`
