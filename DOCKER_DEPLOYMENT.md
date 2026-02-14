# 🐳 Despliegue con Docker - Sistema de Monitoreo con RabbitMQ

Esta guía te explica cómo ejecutar todos los servicios usando Docker Compose.

---

## 📋 Prerrequisitos

### Software Requerido

```bash
# Docker
docker --version
# Debe ser >= 20.10

# Docker Compose
docker compose version
# Debe ser >= 2.0
```

### Hardware

- RAM: Mínimo 4GB, recomendado 8GB
- Disco: 5GB libres
- CPU: 2 cores mínimo

### Verificar Wallet de Oracle

```bash
# IMPORTANTE: El wallet debe existir
ls -la consumer-location/.credentials/Wallet_GIBI3XSETA997Y7I/

# Si no existe, copiar desde backend
cp -r backend/.credentials consumer-location/
```

---

## 🚀 Iniciar Servicios

### Opción 1: Levantar Todo a la Vez (Recomendado)

```bash
# Ir al directorio raíz del proyecto
cd /home/rmji/Projects/duoc/b5/cn1/exp2

# Construir imágenes y levantar todos los servicios
docker compose up -d --build

# Ver logs en tiempo real
docker compose logs -f
```

**Tiempo estimado:** 3-5 minutos (primera vez)

### Opción 2: Levantar Paso a Paso

#### Paso 1: Solo RabbitMQ

```bash
docker compose up -d rabbitmq

# Esperar a que esté healthy (10-15 segundos)
docker compose ps rabbitmq
```

#### Paso 2: Productores

```bash
docker compose up -d producer-gps producer-schedule

# Ver logs
docker compose logs -f producer-gps producer-schedule
```

#### Paso 3: Consumidores

```bash
docker compose up -d consumer-location consumer-schedule

# Ver logs
docker compose logs -f consumer-location consumer-schedule
```

### Opción 3: Levantar un Servicio Específico

```bash
# Solo Producer GPS
docker compose up -d producer-gps

# Ver sus logs
docker compose logs -f producer-gps
```

---

## ✅ Verificar que Todo Esté Corriendo

### Ver Estado de Contenedores

```bash
docker compose ps

# Salida esperada:
# NAME                STATUS
# consumer-location   Up (healthy)
# consumer-schedule   Up
# producer-gps        Up
# producer-schedule   Up
# rabbitmq            Up (healthy)
```

Todos deben estar en estado **Up**.

### Health Checks

```bash
# Producer GPS
curl http://localhost:8082/api/producer/gps/health

# Producer Schedule
curl http://localhost:8083/api/producer/schedule/health

# RabbitMQ Management UI
open http://localhost:15672
# Usuario: admin
# Password: admin123
```

### Ver Logs

```bash
# Todos los servicios
docker compose logs

# Últimas 50 líneas
docker compose logs --tail 50

# Seguir en tiempo real
docker compose logs -f

# Servicio específico
docker compose logs -f consumer-location

# Buscar errores
docker compose logs | grep -i error
```

---

## 🧪 Probar el Sistema

### Test Manual - Flujo GPS

```bash
# Enviar ubicación GPS
curl -X POST http://localhost:8082/api/producer/gps/location \
  -H "Content-Type: application/json" \
  -d '{
    "vehicleId": "BUS-DOCKER-001",
    "latitude": -33.4489,
    "longitude": -70.6693,
    "timestamp": "2026-02-14T15:30:00",
    "speed": 45.5,
    "route": "ROUTE-101"
  }'

# Verificar logs del consumer
docker logs consumer-location 2>&1 | tail -20
```

**Logs esperados:**
```
Received location message for vehicle: BUS-DOCKER-001
Location processed successfully for vehicle: BUS-DOCKER-001
```

### Test Manual - Flujo Schedule

```bash
# Enviar actualización de horario
curl -X POST http://localhost:8083/api/producer/schedule/update \
  -H "Content-Type: application/json" \
  -d '{
    "updateId": "DOCKER-TEST-001",
    "vehicleId": "BUS-DOCKER-001",
    "routeId": "ROUTE-101",
    "scheduleType": "ARRIVAL",
    "scheduledTime": "2026-02-14T10:45:00",
    "actualTime": "2026-02-14T10:47:00",
    "status": "DELAYED",
    "timestamp": "2026-02-14T10:47:00"
  }'

# Verificar archivo JSON generado
docker exec consumer-schedule ls -lh /app/data/schedules/
docker exec consumer-schedule cat /app/data/schedules/schedule_*.json | tail -30
```

### Test Automatizado Completo

```bash
# Ejecutar suite de tests
./test-all.sh
```

---

## 🛑 Detener Servicios

### Detener Todos los Servicios

```bash
# Detener contenedores (conserva datos)
docker compose stop

# Verificar que estén detenidos
docker compose ps -a
```

### Detener y Eliminar Contenedores

```bash
# Detener y eliminar contenedores (conserva imágenes y volúmenes)
docker compose down

# Detener, eliminar contenedores Y volúmenes (⚠️ BORRA DATOS)
docker compose down -v
```

### Detener un Servicio Específico

```bash
# Detener solo Producer GPS
docker compose stop producer-gps

# Detener y eliminar solo Producer GPS
docker compose rm -sf producer-gps
```

---

## 🔄 Actualizar Servicios

### Escenario 1: Código Modificado

Si modificaste el código de un servicio:

```bash
# Opción A: Reconstruir y reiniciar un servicio específico
docker compose build producer-gps
docker compose up -d producer-gps

# Opción B: Comando combinado
docker compose up -d --build producer-gps

# Opción C: Reconstruir todos
docker compose up -d --build
```

### Escenario 2: Cambios en docker-compose.yml

Si modificaste `docker-compose.yml`:

```bash
# Recrear contenedores con la nueva configuración
docker compose up -d --force-recreate

# O detener, eliminar y levantar
docker compose down
docker compose up -d
```

### Escenario 3: Cambios en Variables de Entorno

Si modificaste variables de entorno en `docker-compose.yml`:

```bash
# Recrear solo el servicio afectado
docker compose up -d --force-recreate consumer-location
```

### Escenario 4: Actualizar Wallet de Oracle

Si actualizaste el wallet en `consumer-location/.credentials/`:

```bash
# Reconstruir imagen del consumer-location
docker compose build consumer-location

# Reiniciar con nueva imagen
docker compose up -d consumer-location

# Verificar logs
docker compose logs -f consumer-location
```

---

## 🔧 Comandos de Mantenimiento

### Limpiar Imágenes Antiguas

```bash
# Ver imágenes
docker images | grep exp2

# Eliminar imágenes antiguas (sin tag)
docker image prune

# Eliminar todas las imágenes no usadas
docker image prune -a
```

### Limpiar Volúmenes

```bash
# Listar volúmenes
docker volume ls | grep exp2

# Ver contenido de volumen de schedules
docker run --rm -v exp2_schedules-data:/data alpine ls -lh /data

# Backup de volumen
docker run --rm -v exp2_schedules-data:/data -v $(pwd):/backup alpine \
  tar czf /backup/schedules-backup-$(date +%Y%m%d).tar.gz /data

# Eliminar volúmenes no usados
docker volume prune
```

### Limpiar Sistema Completo

```bash
# ⚠️ CUIDADO: Elimina todo lo no usado
docker system prune -a --volumes

# Ver espacio liberado
docker system df
```

---

## 📊 Monitoreo

### Ver Uso de Recursos

```bash
# CPU y RAM en tiempo real
docker stats

# Solo los servicios del proyecto
docker stats rabbitmq producer-gps producer-schedule consumer-location consumer-schedule
```

### Inspeccionar un Contenedor

```bash
# Ver configuración completa
docker inspect consumer-location

# Ver solo variables de entorno
docker inspect consumer-location | jq '.[0].Config.Env'

# Ver volúmenes montados
docker inspect consumer-location | jq '.[0].Mounts'
```

### Acceder a un Contenedor

```bash
# Entrar al contenedor
docker exec -it consumer-schedule sh

# Dentro del contenedor:
ls -la /app/data/schedules/
cat /app/data/schedules/schedule_*.json | tail -30
exit
```

### Ver Logs Detallados

```bash
# Ver logs de RabbitMQ
docker logs rabbitmq

# Ver logs de un periodo específico
docker logs --since 10m consumer-location

# Buscar un vehicleId específico en logs
docker logs consumer-location 2>&1 | grep "BUS-001"

# Guardar logs en archivo
docker compose logs > logs-$(date +%Y%m%d-%H%M%S).txt
```

---

## 🔍 Troubleshooting

### Problema: Contenedor no inicia

```bash
# Ver logs del contenedor
docker compose logs consumer-location

# Ver últimos 100 eventos
docker compose logs --tail 100 consumer-location

# Ver errores específicos
docker compose logs consumer-location | grep -i error
```

### Problema: RabbitMQ no está healthy

```bash
# Ver estado detallado
docker compose ps rabbitmq

# Ver logs de RabbitMQ
docker logs rabbitmq

# Reiniciar RabbitMQ
docker compose restart rabbitmq

# Si persiste, recrear
docker compose stop rabbitmq
docker compose rm -f rabbitmq
docker compose up -d rabbitmq
```

### Problema: Consumer Location no conecta a Oracle

**1. Verificar logs:**
```bash
docker logs consumer-location 2>&1 | grep -i oracle
docker logs consumer-location 2>&1 | grep -i "ORA-"
```

**2. Verificar wallet en contenedor:**
```bash
docker exec consumer-location ls -la /app/.credentials/Wallet_GIBI3XSETA997Y7I/
```

**3. Verificar variable TNS_ADMIN:**
```bash
docker exec consumer-location env | grep TNS_ADMIN
```

**4. Si falla, reconstruir imagen:**
```bash
# Verificar wallet en host
ls -la consumer-location/.credentials/Wallet_GIBI3XSETA997Y7I/

# Reconstruir
docker compose build consumer-location
docker compose up -d consumer-location
```

**5. Verificar Oracle Cloud DB activa:**
- Ir a Oracle Cloud Console
- Verificar estado de BD: **AVAILABLE**

### Problema: Puerto ya en uso

```bash
# Ver qué proceso usa el puerto 8082
lsof -ti:8082

# Si es otro contenedor Docker
docker ps | grep 8082

# Detener contenedor que usa el puerto
docker stop <container-id>

# O cambiar puerto en docker-compose.yml:
# ports:
#   - "9082:8082"  # Usar 9082 en host
```

### Problema: Contenedor usa mucha RAM

```bash
# Ver uso actual
docker stats consumer-location --no-stream

# Limitar RAM (editar docker-compose.yml):
# deploy:
#   resources:
#     limits:
#       memory: 512M
#     reservations:
#       memory: 256M

# Reiniciar con nuevo límite
docker compose up -d --force-recreate consumer-location
```

### Problema: Mensaje no llega al consumer

**1. Verificar cola en RabbitMQ UI:**
- Ir a http://localhost:15672
- Tab "Queues"
- Buscar `gps.locations.queue` o `schedule.updates.queue`
- Ver columna "Messages ready"

**2. Verificar conexión del consumer:**
```bash
# Ver logs de conexión
docker logs consumer-location 2>&1 | grep -i "rabbitmq\|connection"
```

**3. Reiniciar consumer:**
```bash
docker compose restart consumer-location
```

---

## 🔐 Seguridad en Producción

### Cambiar Credenciales de RabbitMQ

1. **Crear archivo `.env` en la raíz:**
```bash
cat > .env << EOF
RABBITMQ_USER=admin_prod
RABBITMQ_PASS=$(openssl rand -base64 32)
EOF
```

2. **Modificar docker-compose.yml:**
```yaml
environment:
  RABBITMQ_DEFAULT_USER: ${RABBITMQ_USER}
  RABBITMQ_DEFAULT_PASS: ${RABBITMQ_PASS}
```

3. **Reiniciar servicios:**
```bash
docker compose down
docker compose up -d
```

### Cambiar Credenciales de Oracle

1. **Editar docker-compose.yml:**
```yaml
environment:
  SPRING_DATASOURCE_USERNAME: ${ORACLE_USER}
  SPRING_DATASOURCE_PASSWORD: ${ORACLE_PASS}
```

2. **Agregar a `.env`:**
```bash
ORACLE_USER=ADMIN
ORACLE_PASS=tu_password_seguro
```

3. **Reiniciar:**
```bash
docker compose up -d consumer-location
```

---

## 📈 Escalabilidad

### Levantar Múltiples Instancias de un Consumer

```bash
# Escalar Consumer Location a 3 instancias
docker compose up -d --scale consumer-location=3

# Verificar
docker compose ps consumer-location

# Ver logs de todas las instancias
docker compose logs -f consumer-location
```

RabbitMQ distribuirá automáticamente los mensajes entre las instancias (Round-robin).

### Configurar Restart Policy

Ya configurado en `docker-compose.yml`:

```yaml
restart: unless-stopped
```

Esto significa que el contenedor se reiniciará automáticamente si:
- Falla por error
- Se reinicia el sistema
- Docker daemon se reinicia

**NO** se reiniciará si lo detienes manualmente con `docker compose stop`.

---

## 📚 Comandos de Referencia Rápida

```bash
# ============== INICIAR ==============
docker compose up -d --build              # Levantar todo
docker compose up -d producer-gps         # Levantar uno solo
docker compose logs -f                    # Ver logs

# ============== VERIFICAR ==============
docker compose ps                         # Estado de servicios
curl http://localhost:8082/api/producer/gps/health
open http://localhost:15672               # RabbitMQ UI

# ============== ACTUALIZAR ==============
docker compose up -d --build <servicio>   # Actualizar uno
docker compose up -d --build              # Actualizar todos
docker compose up -d --force-recreate     # Recrear sin rebuild

# ============== DETENER ==============
docker compose stop                       # Detener todo
docker compose stop producer-gps          # Detener uno
docker compose down                       # Detener y eliminar
docker compose down -v                    # Detener y eliminar TODO

# ============== LOGS ==============
docker compose logs -f                    # Todos en tiempo real
docker compose logs -f consumer-location  # Uno específico
docker compose logs --tail 100            # Últimas 100 líneas
docker logs consumer-location | grep ERROR # Buscar errores

# ============== MANTENIMIENTO ==============
docker compose restart <servicio>         # Reiniciar
docker image prune -a                     # Limpiar imágenes
docker volume prune                       # Limpiar volúmenes
docker system prune -a --volumes          # Limpiar TODO (⚠️)

# ============== DEBUGGING ==============
docker exec -it consumer-schedule sh      # Entrar al contenedor
docker stats                              # Ver uso de recursos
docker inspect consumer-location          # Ver configuración
```

---

## 🔄 Workflow de Desarrollo Típico

### 1. Desarrollo Inicial

```bash
# Levantar todo
docker compose up -d --build

# Ver logs
docker compose logs -f

# Probar
./test-all.sh
```

### 2. Modificar Código de Producer GPS

```bash
# Editar código en producer-gps/src/...

# Reconstruir solo ese servicio
docker compose build producer-gps

# Reiniciar
docker compose up -d producer-gps

# Ver logs del nuevo servicio
docker compose logs -f producer-gps

# Probar
curl -X POST http://localhost:8082/api/producer/gps/location \
  -H "Content-Type: application/json" \
  -d '{"vehicleId":"TEST","latitude":-33.4489,"longitude":-70.6693,"timestamp":"2026-02-14T15:30:00"}'
```

### 3. Modificar Configuración

```bash
# Editar docker-compose.yml

# Recrear servicios
docker compose up -d --force-recreate

# Verificar
docker compose ps
```

### 4. Limpiar y Empezar de Cero

```bash
# Detener y eliminar todo
docker compose down -v

# Limpiar imágenes
docker image prune -a

# Levantar de nuevo
docker compose up -d --build
```

---

## 🆘 Soporte

Si encuentras problemas:

1. **Ver logs detallados:** `docker compose logs -f <servicio>`
2. **Verificar RabbitMQ UI:** http://localhost:15672
3. **Verificar Oracle Cloud:** Base de datos en estado AVAILABLE
4. **Consultar** `DEPLOYMENT_GUIDE.md` para troubleshooting completo
5. **Recrear servicio:** `docker compose up -d --force-recreate <servicio>`

---

## 📖 Documentación Relacionada

- `LOCAL_DEVELOPMENT.md` - Desarrollo local sin Docker
- `DEPLOYMENT_GUIDE.md` - Guía completa de despliegue
- `README_RABBITMQ.md` - Arquitectura y documentación técnica
- `TEST_SCRIPTS.md` - Scripts de prueba

---

**Última actualización:** 2026-02-14  
**Versión:** 1.0.0
