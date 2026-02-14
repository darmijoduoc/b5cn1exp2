# Consumer Schedule - Schedule Updates File Consumer

Microservicio consumidor que escucha mensajes de actualizaciones de horarios desde RabbitMQ y los almacena como archivos JSON.

## Tecnologías

- Java 21
- Spring Boot 3.4.1
- Spring AMQP (RabbitMQ)
- Jackson (JSON)
- Maven
- Lombok

## Puerto

`8085`

## Funcionalidad

Este servicio:
1. Escucha la cola `schedule.updates.queue` de RabbitMQ
2. Recibe mensajes con actualizaciones de horarios
3. Serializa los mensajes a JSON con formato pretty-print
4. Guarda cada mensaje como archivo JSON individual en el filesystem

## Formato de Archivos

- **Directorio**: `/app/data/schedules/`
- **Nombre**: `schedule_YYYYMMDD_HHmmss_UUID.json`
- **Formato**: JSON con indentación

Ejemplo: `schedule_20260214_103045_a1b2c3d4.json`

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

## Variables de Entorno

### RabbitMQ
- `RABBITMQ_HOST`: Host de RabbitMQ (default: localhost)
- `RABBITMQ_PORT`: Puerto de RabbitMQ (default: 5672)
- `RABBITMQ_USER`: Usuario de RabbitMQ (default: admin)
- `RABBITMQ_PASS`: Contraseña de RabbitMQ (default: admin123)

### File Storage
- `FILE_STORAGE_PATH`: Ruta donde guardar JSON (default: /app/data/schedules)

## Ejecutar Localmente

```bash
./mvnw clean package
./mvnw spring-boot:run
```

Los archivos JSON se crearán en `/app/data/schedules/` (crear el directorio si no existe).

## Docker

```bash
docker build -t consumer-schedule:latest .
docker run -d \
  --name consumer-schedule \
  -p 8085:8085 \
  -e RABBITMQ_HOST=rabbitmq \
  -v schedules-data:/app/data/schedules \
  consumer-schedule:latest
```

## Cola RabbitMQ

Este servicio consume mensajes de la cola: `schedule.updates.queue`

## Verificación

Para verificar que está funcionando:

1. Ver logs: `docker logs -f consumer-schedule`
2. Enviar mensaje desde Producer Schedule
3. Acceder al contenedor y verificar archivos:
```bash
docker exec -it consumer-schedule sh
ls -la /app/data/schedules/
cat /app/data/schedules/schedule_*.json
```

## Volumen Docker

Los archivos JSON persisten en el volumen Docker `schedules-data`. Para verlos desde el host:

```bash
docker run --rm -v schedules-data:/data alpine ls -la /data
```
