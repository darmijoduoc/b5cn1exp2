# 🖥️ Desarrollo Local - Sistema de Monitoreo con RabbitMQ

Esta guía te explica cómo ejecutar todos los servicios localmente usando los scripts `run-local.sh`.

---

## 📋 Prerrequisitos

### Software Requerido

```bash
# Java 21
java --version

# Maven (incluido con mvnw)
./mvnw --version

# Docker (solo para RabbitMQ)
docker --version
```

### Hardware

- RAM: Mínimo 4GB
- Disco: 2GB libres

---

## 🚀 Iniciar Servicios

### Paso 1: Levantar RabbitMQ

**RabbitMQ debe estar corriendo PRIMERO**. Tienes 2 opciones:

#### Opción A: Con docker-compose (Recomendado)

```bash
# Ir al directorio raíz del proyecto
cd /home/rmji/Projects/duoc/b5/cn1/exp2

# Levantar solo RabbitMQ
docker compose up -d rabbitmq

# Verificar que esté corriendo
docker compose ps rabbitmq
```

#### Opción B: Con docker run

```bash
docker run -d --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  -e RABBITMQ_DEFAULT_USER=admin \
  -e RABBITMQ_DEFAULT_PASS=admin123 \
  rabbitmq:3.13-management

# Verificar
docker ps | grep rabbitmq
```

**Espera 10-15 segundos** para que RabbitMQ inicie completamente.

### Paso 2: Levantar Servicios Spring Boot

Abre **4 terminales** y ejecuta en cada una:

#### Terminal 1: Producer GPS (Puerto 8082)

```bash
cd /home/rmji/Projects/duoc/b5/cn1/exp2/producer-gps
./run-local.sh
```

**Logs esperados:**
```
✓ RabbitMQ: Corriendo
✓ Puerto 8082: Disponible
✓ Maven wrapper: Encontrado
🚀 Iniciando Producer GPS...
```

#### Terminal 2: Producer Schedule (Puerto 8083)

```bash
cd /home/rmji/Projects/duoc/b5/cn1/exp2/producer-schedule
./run-local.sh
```

**Logs esperados:**
```
✓ RabbitMQ: Corriendo
✓ Puerto 8083: Disponible
✓ Maven wrapper: Encontrado
🚀 Iniciando Producer Schedule...
```

#### Terminal 3: Consumer Location (Puerto 8084)

```bash
cd /home/rmji/Projects/duoc/b5/cn1/exp2/consumer-location
./run-local.sh
```

**Logs esperados:**
```
✓ RabbitMQ: Corriendo
✓ Oracle Wallet: Encontrado
✓ Puerto 8084: Disponible
⚠️  NOTA: Asegúrate que Oracle Cloud DB esté activa
🚀 Iniciando Consumer Location...
```

**⚠️ IMPORTANTE:** Verifica que la base de datos Oracle esté **AVAILABLE** en Oracle Cloud Console.

#### Terminal 4: Consumer Schedule (Puerto 8085)

```bash
cd /home/rmji/Projects/duoc/b5/cn1/exp2/consumer-schedule
./run-local.sh
```

**Logs esperados:**
```
✓ RabbitMQ: Corriendo
✓ Directorio storage: Creado (/tmp/schedules)
✓ Puerto 8085: Disponible
🚀 Iniciando Consumer Schedule...
```

### Paso 3: Verificar que Todo Esté Corriendo

```bash
# Health check de productores
curl http://localhost:8082/api/producer/gps/health
curl http://localhost:8083/api/producer/schedule/health

# RabbitMQ Management UI
open http://localhost:15672
# Usuario: admin
# Password: admin123
```

---

## 🧪 Probar el Sistema

### Test Rápido - Flujo GPS

```bash
# Enviar ubicación GPS
curl -X POST http://localhost:8082/api/producer/gps/location \
  -H "Content-Type: application/json" \
  -d '{
    "vehicleId": "BUS-LOCAL-001",
    "latitude": -33.4489,
    "longitude": -70.6693,
    "timestamp": "2026-02-14T15:30:00",
    "speed": 45.5,
    "route": "ROUTE-101"
  }'
```

**Verificar logs:**
- **Terminal 1 (Producer GPS):** `Location published for vehicle: BUS-LOCAL-001`
- **Terminal 3 (Consumer Location):** `Received location message for vehicle: BUS-LOCAL-001`
- **Terminal 3 (Consumer Location):** `Location processed successfully for vehicle: BUS-LOCAL-001`

### Test Rápido - Flujo Schedule

```bash
# Enviar actualización de horario
curl -X POST http://localhost:8083/api/producer/schedule/update \
  -H "Content-Type: application/json" \
  -d '{
    "updateId": "LOCAL-TEST-001",
    "vehicleId": "BUS-LOCAL-001",
    "routeId": "ROUTE-101",
    "scheduleType": "ARRIVAL",
    "scheduledTime": "2026-02-14T10:45:00",
    "actualTime": "2026-02-14T10:47:00",
    "status": "DELAYED",
    "timestamp": "2026-02-14T10:47:00"
  }'
```

**Verificar logs:**
- **Terminal 2 (Producer Schedule):** `Schedule update published: LOCAL-TEST-001`
- **Terminal 4 (Consumer Schedule):** `Received schedule update: ID=LOCAL-TEST-001`
- **Terminal 4 (Consumer Schedule):** `Schedule update saved as JSON: LOCAL-TEST-001`

**Verificar archivo JSON creado:**
```bash
ls -lh /tmp/schedules/
cat /tmp/schedules/schedule_*.json | tail -30
```

### Test Automatizado Completo

```bash
# Ejecutar suite de tests
cd /home/rmji/Projects/duoc/b5/cn1/exp2
./test-all.sh
```

---

## 🛑 Detener Servicios

### Detener Servicios Spring Boot

En cada terminal donde esté corriendo un servicio:

```bash
# Presionar Ctrl+C
```

Verás algo como:
```
^C
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### Detener RabbitMQ

#### Si usaste docker-compose:

```bash
cd /home/rmji/Projects/duoc/b5/cn1/exp2
docker compose stop rabbitmq
```

#### Si usaste docker run:

```bash
docker stop rabbitmq
```

### Detener Todo a la Vez

```bash
# 1. Ctrl+C en las 4 terminales de Spring Boot

# 2. Detener RabbitMQ
docker compose stop rabbitmq
# o
docker stop rabbitmq
```

---

## 🔄 Reiniciar un Servicio

### Reiniciar Servicio Spring Boot

```bash
# En la terminal del servicio:
# 1. Presionar Ctrl+C para detener
# 2. Ejecutar nuevamente
./run-local.sh
```

### Reiniciar RabbitMQ

```bash
docker compose restart rabbitmq
# o
docker restart rabbitmq
```

---

## 🔧 Troubleshooting

### Problema: "Puerto ya en uso"

```bash
# Ver qué proceso usa el puerto (ejemplo: 8082)
lsof -ti:8082

# Matar el proceso
kill -9 $(lsof -ti:8082)

# Reintentar
./run-local.sh
```

### Problema: "RabbitMQ no responde"

```bash
# Ver estado
docker ps | grep rabbitmq

# Si está detenido, iniciar
docker start rabbitmq

# Si no existe, crear
docker compose up -d rabbitmq

# Ver logs
docker logs rabbitmq
```

### Problema: "Consumer Location no conecta a Oracle"

**Verificar:**

1. **Base de datos Oracle activa:**
   - Ir a Oracle Cloud Console
   - Verificar estado: AVAILABLE

2. **Wallet presente:**
   ```bash
   ls -la consumer-location/.credentials/Wallet_GIBI3XSETA997Y7I/
   ```
   
   Si falta, copiar desde backend:
   ```bash
   cp -r backend/.credentials consumer-location/
   ```

3. **Credenciales correctas:**
   - Revisar `consumer-location/src/main/resources/application.properties`
   - Variables: `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD`

### Problema: "Consumer Schedule no guarda archivos"

```bash
# Verificar directorio existe
ls -la /tmp/schedules

# Si no existe, crearlo
mkdir -p /tmp/schedules

# Verificar permisos
chmod 755 /tmp/schedules
```

### Problema: "No veo logs detallados"

Edita el `application.properties` del servicio y cambia:

```properties
logging.level.root=DEBUG
```

Reinicia el servicio.

---

## 🎯 Tips de Desarrollo


### Cambiar Puerto de un Servicio

Edita el `application.properties`:

```properties
# Ejemplo: cambiar Producer GPS a 9082
server.port=9082
```

Luego reinicia el servicio.

### Ver Logs de RabbitMQ

```bash
# Logs en tiempo real
docker logs -f rabbitmq

# Últimas 100 líneas
docker logs --tail 100 rabbitmq
```

### Limpiar Colas de RabbitMQ

Si las colas tienen mensajes atascados:

1. Ir a http://localhost:15672
2. Tab "Queues"
3. Click en la cola
4. Click "Purge Messages"

O por comando:

```bash
docker exec rabbitmq rabbitmqctl purge_queue gps.locations.queue
docker exec rabbitmq rabbitmqctl purge_queue schedule.updates.queue
```

---

## 📊 Monitoreo en Desarrollo

### Ver Recursos Usados

```bash
# CPU y RAM
top

# Solo procesos Java
ps aux | grep java
```

### Ver Archivos JSON Generados

```bash
# Listar
ls -lh /tmp/schedules/

# Ver últimos 3 archivos
ls -lt /tmp/schedules/ | head -4

# Ver contenido del último
cat $(ls -t /tmp/schedules/*.json | head -1) | jq .
```

### Queries de RabbitMQ Management

```bash
# Ver colas
curl -u admin:admin123 http://localhost:15672/api/queues | jq '.[] | {name: .name, messages: .messages}'

# Ver conexiones
curl -u admin:admin123 http://localhost:15672/api/connections | jq '.[] | {name: .name, state: .state}'
```

---

## 🔐 Configuración de Entorno

Los scripts `run-local.sh` configuran automáticamente estas variables:

```bash
# RabbitMQ
export RABBITMQ_HOST=localhost
export RABBITMQ_PORT=5672
export RABBITMQ_USER=admin
export RABBITMQ_PASS=admin123

# Oracle (solo Consumer Location)
export TNS_ADMIN=$PWD/.credentials/Wallet_GIBI3XSETA997Y7I
export SPRING_DATASOURCE_URL=jdbc:oracle:thin:@gibi3xseta997y7i_tp
export SPRING_DATASOURCE_USERNAME=ADMIN
export SPRING_DATASOURCE_PASSWORD=aWxpYqvej@bUin3P!tbP

# Storage (solo Consumer Schedule)
export FILE_STORAGE_PATH=/tmp/schedules
```

Si necesitas cambiar alguna, puedes:

1. **Editar el script `run-local.sh`**, o
2. **Exportar antes de ejecutar:**
   ```bash
   export RABBITMQ_HOST=192.168.1.100
   ./run-local.sh
   ```

---

## 📚 Comandos de Referencia Rápida

```bash
# INICIAR TODO
docker compose up -d rabbitmq
cd producer-gps && ./run-local.sh           # Terminal 1
cd producer-schedule && ./run-local.sh      # Terminal 2
cd consumer-location && ./run-local.sh      # Terminal 3
cd consumer-schedule && ./run-local.sh      # Terminal 4

# HEALTH CHECKS
curl http://localhost:8082/api/producer/gps/health
curl http://localhost:8083/api/producer/schedule/health

# DETENER TODO
# Ctrl+C en cada terminal
docker compose stop rabbitmq

# VER LOGS RABBITMQ
docker logs -f rabbitmq

# VER ARCHIVOS JSON
ls -lh /tmp/schedules/

# TEST RÁPIDO
./test-all.sh
```

---

## 🆘 Soporte

Si encuentras problemas:

1. **Verifica logs** en cada terminal
2. **Revisa RabbitMQ UI**: http://localhost:15672
3. **Consulta** `DEPLOYMENT_GUIDE.md` para troubleshooting detallado
4. **Revisa** configuración en cada `application.properties`

---

**Última actualización:** 2026-02-14  
**Versión:** 1.0.0
