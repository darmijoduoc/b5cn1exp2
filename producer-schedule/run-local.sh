#!/bin/bash

# Script de ejecución local para Producer Schedule
# Puerto: 8083

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

clear
echo -e "${BLUE}╔════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║   Producer Schedule - Ejecución Local     ║${NC}"
echo -e "${BLUE}║   Puerto: 8083                             ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════╝${NC}"
echo ""

# Verificar dependencias
echo -e "${YELLOW}📋 Verificando dependencias...${NC}"

# 1. Verificar RabbitMQ
echo -n "  • RabbitMQ: "
if docker ps --format '{{.Names}}' | grep -q '^rabbitmq$'; then
    echo -e "${GREEN}✓ Corriendo${NC}"
elif docker ps -a --format '{{.Names}}' | grep -q '^rabbitmq$'; then
    echo -e "${YELLOW}⚠ Detenido - Iniciando...${NC}"
    docker start rabbitmq
    sleep 3
else
    echo -e "${RED}✗ No encontrado${NC}"
    echo ""
    echo -e "${YELLOW}Iniciando RabbitMQ...${NC}"
    docker run -d --name rabbitmq \
        -p 5672:5672 -p 15672:15672 \
        -e RABBITMQ_DEFAULT_USER=admin \
        -e RABBITMQ_DEFAULT_PASS=admin123 \
        rabbitmq:3.13-management
    echo -e "${GREEN}RabbitMQ iniciado. Esperando 10 segundos...${NC}"
    sleep 10
fi

# 2. Verificar puerto disponible
echo -n "  • Puerto 8083: "
if lsof -Pi :8083 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo -e "${RED}✗ En uso${NC}"
    echo ""
    echo -e "${RED}ERROR: Puerto 8083 ya está en uso${NC}"
    echo "Detén el proceso que lo usa o usa otro puerto"
    exit 1
else
    echo -e "${GREEN}✓ Disponible${NC}"
fi

# 3. Verificar Maven wrapper
echo -n "  • Maven wrapper: "
if [ -f "./mvnw" ]; then
    echo -e "${GREEN}✓ Encontrado${NC}"
else
    echo -e "${RED}✗ No encontrado${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}✅ Todas las dependencias OK${NC}"
echo ""

# Configuración de entorno
echo -e "${YELLOW}🔧 Configuración:${NC}"
echo "  • RABBITMQ_HOST: localhost"
echo "  • RABBITMQ_PORT: 5672"
echo "  • RABBITMQ_USER: admin"
echo "  • RABBITMQ_PASS: admin123"
echo ""

# Exportar variables (valores por defecto)
export RABBITMQ_HOST=localhost
export RABBITMQ_PORT=5672
export RABBITMQ_USER=admin
export RABBITMQ_PASS=admin123

echo -e "${BLUE}════════════════════════════════════════════${NC}"
echo -e "${GREEN}🚀 Iniciando Producer Schedule...${NC}"
echo -e "${BLUE}════════════════════════════════════════════${NC}"
echo ""
echo -e "${YELLOW}Endpoints disponibles:${NC}"
echo "  • POST http://localhost:8083/api/producer/schedule/update"
echo "  • POST http://localhost:8083/api/producer/schedule/updates/batch"
echo "  • GET  http://localhost:8083/api/producer/schedule/health"
echo ""
echo -e "${YELLOW}RabbitMQ Management UI:${NC}"
echo "  • http://localhost:15672 (admin/admin123)"
echo ""
echo -e "${YELLOW}Presiona Ctrl+C para detener${NC}"
echo ""
echo -e "${BLUE}────────────────────────────────────────────${NC}"

# Ejecutar servicio
./mvnw spring-boot:run
