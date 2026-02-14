#!/bin/bash

# Script de despliegue rápido para EXP2 Backend

set -e

APP_NAME="exp3-backend"
IMAGE_NAME="exp3-backend:latest"
PORT=8080

echo "🚀 Iniciando despliegue de $APP_NAME..."

# Detener y eliminar contenedor existente si existe
if [ "$(docker ps -aq -f name=$APP_NAME)" ]; then
    echo "⏹️  Deteniendo contenedor existente..."
    docker stop $APP_NAME 2>/dev/null || true
    echo "🗑️  Eliminando contenedor existente..."
    docker rm $APP_NAME 2>/dev/null || true
fi

# Construir imagen
echo "🔨 Construyendo imagen Docker..."
docker build -t $IMAGE_NAME .

# Ejecutar contenedor
echo "▶️  Iniciando contenedor..."
if [ -f .env ]; then
    echo "📄 Usando archivo .env para variables de entorno"
    docker run -d \
        --name $APP_NAME \
        -p $PORT:8080 \
        --env-file .env \
        $IMAGE_NAME
else
    echo "⚠️  No se encontró archivo .env, usando valores por defecto"
    docker run -d \
        --name $APP_NAME \
        -p $PORT:8080 \
        -e SPRING_DATASOURCE_URL=jdbc:oracle:thin:@gibi3xseta997y7i_tp \
        -e SPRING_DATASOURCE_USERNAME=ADMIN \
        -e SPRING_DATASOURCE_PASSWORD=aWxpYqvej@bUin3P!tbP \
        $IMAGE_NAME
fi

echo "✅ Despliegue completado!"
echo "📊 Ver logs: docker logs -f $APP_NAME"
echo "🌐 Aplicación disponible en: http://localhost:$PORT"
echo ""
echo "Esperando que la aplicación inicie..."
sleep 5
docker logs --tail 20 $APP_NAME

