# Guía de Despliegue en AWS

## 📋 Requisitos Previos

1. **Instancia EC2** con:
   - Ubuntu 22.04 LTS o superior
   - Docker y Docker Compose instalados
   - Puertos abiertos en Security Group:
     - 5672 (RabbitMQ AMQP)
     - 15672 (RabbitMQ Management)
     - 8082 (Producer GPS)
     - 8083 (Producer Schedule)
     - 8084 (Consumer Location)
     - 8085 (Consumer Schedule)

2. **Imágenes publicadas** en Docker Hub:
   - `dparmijog/cn1_exp2_producer_gps:latest`
   - `dparmijog/cn1_exp2_producer_schedule:latest`
   - `dparmijog/cn1_exp2_consumer_location:latest`
   - `dparmijog/cn1_exp2_consumer_schedule:latest`

---

## 🚀 Despliegue Paso a Paso

### 1. Conectarse a la instancia EC2

```bash
ssh -i "tu-key.pem" ubuntu@ec2-xx-xx-xx-xx.compute.amazonaws.com
```

### 2. Instalar Docker y Docker Compose (si no están instalados)

```bash
# Actualizar paquetes
sudo apt update

# Instalar Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Agregar usuario al grupo docker
sudo usermod -aG docker $USER

# Instalar Docker Compose
sudo apt install docker-compose -y

# Verificar instalación
docker --version
docker-compose --version

# Reiniciar sesión para aplicar permisos
exit
# Volver a conectarse por SSH
```

### 3. Crear directorio de trabajo

```bash
mkdir -p ~/exp2-rabbitmq
cd ~/exp2-rabbitmq
```

### 4. Crear archivo docker-compose-aws.yml

```bash
nano docker-compose-aws.yml
```

Copiar el contenido del archivo `docker-compose-aws.yml` de este repositorio.

**Importante**: Si tu contraseña de Oracle es diferente, crea un archivo `.env`:

```bash
nano .env
```

Contenido:
```
ORACLE_PASSWORD=tu_password_real
```

### 5. Descargar imágenes y levantar servicios

```bash
# Descargar todas las imágenes
docker compose -f docker-compose-aws.yml pull

# Levantar todos los servicios
docker compose -f docker-compose-aws.yml up -d

# Ver el estado
docker compose -f docker-compose-aws.yml ps
```

### 6. Verificar que todo está funcionando

```bash
# Ver logs de todos los servicios
docker compose -f docker-compose-aws.yml logs -f

# Ver logs de un servicio específico
docker logs -f producer-gps
docker logs -f consumer-location

# Verificar que RabbitMQ está corriendo
curl http://localhost:15672
# Debería devolver la página HTML de RabbitMQ Management
```

### 7. Acceder a RabbitMQ Management UI

Desde tu navegador:
```
http://EC2_PUBLIC_IP:15672
```

Credenciales:
- Usuario: `admin`
- Password: `admin123`

### 8. Probar los productores

**Producer GPS:**
```bash
curl -X POST http://EC2_PUBLIC_IP:8082/api/producer/gps/location \
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

**Producer Schedule:**
```bash
curl -X POST http://EC2_PUBLIC_IP:8083/api/producer/schedule/update \
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

### 9. Verificar procesamiento de mensajes

```bash
# Ver ACKs de Consumer Location
docker logs consumer-location 2>&1 | grep "ACK enviado"

# Ver ACKs de Consumer Schedule
docker logs consumer-schedule 2>&1 | grep "ACK enviado"

# Ver archivos JSON generados
docker exec consumer-schedule ls -lh /app/data/schedules/
```

---

## 🔄 Actualizar Imágenes

Cuando haya cambios en el código:

```bash
# En tu máquina local
cd /home/rmji/Projects/duoc/b5/cn1/exp2

# Reconstruir imágenes
docker compose build

# Re-taggear
docker tag exp2-producer-gps:latest dparmijog/cn1_exp2_producer_gps:latest
docker tag exp2-producer-schedule:latest dparmijog/cn1_exp2_producer_schedule:latest
docker tag exp2-consumer-location:latest dparmijog/cn1_exp2_consumer_location:latest
docker tag exp2-consumer-schedule:latest dparmijog/cn1_exp2_consumer_schedule:latest

# Push a Docker Hub
docker push dparmijog/cn1_exp2_producer_gps:latest
docker push dparmijog/cn1_exp2_producer_schedule:latest
docker push dparmijog/cn1_exp2_consumer_location:latest
docker push dparmijog/cn1_exp2_consumer_schedule:latest
```

**En AWS EC2:**

```bash
cd ~/exp2-rabbitmq

# Detener servicios
docker compose -f docker-compose-aws.yml down

# Descargar nuevas imágenes
docker compose -f docker-compose-aws.yml pull

# Levantar con nuevas imágenes
docker compose -f docker-compose-aws.yml up -d

# Verificar
docker compose -f docker-compose-aws.yml ps
docker logs -f producer-gps
```

---

## 🛑 Detener y Limpiar

```bash
# Detener todos los servicios
docker compose -f docker-compose-aws.yml down

# Detener y eliminar volúmenes (CUIDADO: borra datos)
docker compose -f docker-compose-aws.yml down -v

# Limpiar imágenes antiguas
docker image prune -a
```

---

## 📊 Monitoreo y Logs

### Ver logs en tiempo real

```bash
# Todos los servicios
docker compose -f docker-compose-aws.yml logs -f

# Servicio específico
docker logs -f producer-gps
docker logs -f consumer-location

# Ver solo errores
docker logs consumer-location 2>&1 | grep ERROR
```

### Verificar salud de servicios

```bash
# Estado de contenedores
docker compose -f docker-compose-aws.yml ps

# Estadísticas de uso
docker stats

# Inspeccionar red
docker network inspect exp2-rabbitmq_app-network
```

### Verificar colas en RabbitMQ

```bash
# Via Management API
curl -u admin:admin123 http://localhost:15672/api/queues

# O acceder a http://EC2_PUBLIC_IP:15672 en el navegador
```

---

## 🐛 Troubleshooting

### Problema: Contenedor no inicia

```bash
# Ver logs del contenedor
docker logs nombre-contenedor

# Reiniciar contenedor específico
docker compose -f docker-compose-aws.yml restart nombre-servicio
```

### Problema: No se conecta a RabbitMQ

```bash
# Verificar que RabbitMQ está healthy
docker inspect rabbitmq | grep -A 5 Health

# Ver logs de RabbitMQ
docker logs rabbitmq

# Reiniciar RabbitMQ
docker restart rabbitmq
```

### Problema: Consumer Location no guarda en Oracle

```bash
# Verificar logs del consumer
docker logs consumer-location 2>&1 | grep ERROR

# Verificar variables de entorno
docker inspect consumer-location | grep -A 20 Env

# Verificar wallet de Oracle
docker exec consumer-location ls -la /app/.credentials/Wallet_GIBI3XSETA997Y7I/
```

### Problema: No se pueden crear archivos JSON

```bash
# Verificar permisos del volumen
docker exec consumer-schedule ls -la /app/data/

# Verificar que el directorio existe
docker exec consumer-schedule ls -lh /app/data/schedules/

# Verificar espacio en disco
df -h
```

---

## 🔒 Seguridad

### Cambiar credenciales de RabbitMQ

Editar `docker-compose-aws.yml`:

```yaml
environment:
  RABBITMQ_DEFAULT_USER: tu_usuario
  RABBITMQ_DEFAULT_PASS: tu_password_seguro
```

Actualizar en todos los servicios que se conectan a RabbitMQ:

```yaml
environment:
  RABBITMQ_USER: tu_usuario
  RABBITMQ_PASS: tu_password_seguro
```

### Usar archivo .env para secrets

```bash
# Crear .env
nano .env
```

Contenido:
```
RABBITMQ_USER=admin
RABBITMQ_PASS=password_super_seguro
ORACLE_PASSWORD=password_oracle_seguro
```

Actualizar `docker-compose-aws.yml` para usar variables:

```yaml
environment:
  RABBITMQ_USER: ${RABBITMQ_USER}
  RABBITMQ_PASS: ${RABBITMQ_PASS}
  SPRING_DATASOURCE_PASSWORD: ${ORACLE_PASSWORD}
```

---

## 📈 Escalabilidad

### Escalar consumers horizontalmente

```bash
# Levantar 3 instancias de Consumer Location
docker compose -f docker-compose-aws.yml up -d --scale consumer-location=3

# Verificar
docker compose -f docker-compose-aws.yml ps
```

**Nota**: Los consumers deben estar diseñados para manejar concurrencia. RabbitMQ distribuirá mensajes entre instancias.

---

## 🎯 Arquitectura Desplegada

```
┌─────────────────────────────────────────────────────────┐
│                     AWS EC2 Instance                    │
│                                                         │
│  ┌──────────────┐                                      │
│  │   RabbitMQ   │ :5672, :15672                       │
│  └──────┬───────┘                                      │
│         │                                               │
│  ┌──────┴───────────────┐                              │
│  │                      │                              │
│  ▼                      ▼                              │
│ ┌────────────┐    ┌────────────┐                      │
│ │Producer GPS│    │Producer    │                      │
│ │   :8082    │    │Schedule    │                      │
│ └────────────┘    │   :8083    │                      │
│                   └────────────┘                       │
│                                                         │
│  ┌──────────┬───────────┐                              │
│  │          │           │                              │
│  ▼          ▼           ▼                              │
│ ┌────────────┐    ┌────────────┐                      │
│ │Consumer    │    │Consumer    │                      │
│ │Location    │    │Schedule    │                      │
│ │  :8084     │    │  :8085     │                      │
│ └─────┬──────┘    └──────┬─────┘                      │
│       │                  │                             │
│       ▼                  ▼                             │
│  Oracle DB         JSON Files                          │
│  (Cloud)           (Volume)                            │
└─────────────────────────────────────────────────────────┘
```

---

## ✅ Checklist de Despliegue

- [ ] Instancia EC2 creada y configurada
- [ ] Puertos abiertos en Security Group
- [ ] Docker y Docker Compose instalados
- [ ] Imágenes publicadas en Docker Hub
- [ ] Archivo docker-compose-aws.yml creado
- [ ] Variables de entorno configuradas (.env)
- [ ] Servicios levantados con `docker compose up -d`
- [ ] RabbitMQ Management UI accesible
- [ ] Producers responden a curl
- [ ] Consumers procesan mensajes (verificar logs)
- [ ] Oracle DB conecta correctamente
- [ ] Archivos JSON se generan correctamente

---

## 📚 Referencias

- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [RabbitMQ Management Plugin](https://www.rabbitmq.com/management.html)
- [AWS EC2 Security Groups](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-security-groups.html)
