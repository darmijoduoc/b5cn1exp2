# 🚀 Guía de Despliegue Rápida

## Comandos Esenciales

### Despliegue Simple (1 comando)
```bash
docker-compose up -d
```

### Despliegue con Script
```bash
./deploy.sh
```

### Despliegue Manual
```bash

# Detener y eliminar contenedor previo
docker stop exp3-backend 2>/dev/null || true
docker rm exp3-backend 2>/dev/null || true

docker build -t exp3-backend:latest . && \
docker run -d --name exp3-backend -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:oracle:thin:@gibi3xseta997y7i_tp \
  -e SPRING_DATASOURCE_USERNAME=ADMIN \
  -e SPRING_DATASOURCE_PASSWORD=aWxpYqvej@bUin3P!tbP \
  exp3-backend:latest
```

---

## Comandos Útiles

```bash
# Ver logs
docker logs -f exp3-backend

# Verificar
curl http://localhost:8080/authenticate
```

---

## Troubleshooting

```bash
# Ver estado del contenedor
docker ps -a

# Ver logs de error
docker logs exp3-backend

# Entrar al contenedor
docker exec -it exp3-backend sh

# Limpiar todo
docker stop exp3-backend && docker rm exp3-backend && docker rmi exp3-backend:latest
```

