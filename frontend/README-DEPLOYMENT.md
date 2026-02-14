# Angular Labs Application - Deployment Guide

Esta guía explica cómo desplegar la aplicación Angular Labs utilizando Docker y nginx.

## Prerrequisitos

- Docker instalado en el sistema
- Git (para clonar el repositorio)

## Estructura del Proyecto

```
frontend/
├── Dockerfile
├── package.json
├── pnpm-lock.yaml
├── src/
│   └── app/
│       ├── components/
│       ├── models/
│       └── services/
└── README-DEPLOYMENT.md
```

## Comandos de Despliegue

### 1. Construcción de la Imagen Docker

```bash
# Construir la imagen Docker
docker build -t angular-labs .

# Construir con tag específico
docker build -t angular-labs:v1.0.0 .
```

### 2. Ejecutar el Contenedor

```bash
# Ejecutar en puerto 80
docker run -d -p 80:80 --name angular-labs-app angular-labs

# Ejecutar en puerto personalizado (ej: 8080)
docker run -d -p 8080:80 --name angular-labs-app angular-labs

# Ejecutar con restart automático
docker run -d -p 80:80 --restart unless-stopped --name angular-labs-app angular-labs
```

### 3. Comandos de Gestión del Contenedor

```bash
# Ver contenedores en ejecución
docker ps

# Ver logs del contenedor
docker logs angular-labs-app

# Seguir logs en tiempo real
docker logs -f angular-labs-app

# Parar el contenedor
docker stop angular-labs-app

# Iniciar el contenedor
docker start angular-labs-app

# Eliminar el contenedor
docker rm angular-labs-app

# Eliminar la imagen
docker rmi angular-labs
```

### 4. Despliegue con Docker Compose (Opcional)

Crear archivo `docker-compose.yml`:

```yaml
version: '3.8'

services:
  angular-labs:
    build: .
    ports:
      - "80:80"
    restart: unless-stopped
    container_name: angular-labs-app
```

Comandos Docker Compose:

```bash
# Construir y ejecutar
docker-compose up -d

# Ver logs
docker-compose logs -f

# Parar servicios
docker-compose down

# Reconstruir imagen
docker-compose up -d --build
```

## Desarrollo Local

### Instalación de Dependencias

```bash
# Instalar pnpm globalmente
npm install -g pnpm

# Instalar dependencias del proyecto
pnpm install
```

### Comandos de Desarrollo

```bash
# Ejecutar en modo desarrollo
pnpm start

# Construir para producción
pnpm run build

# Ejecutar tests
pnpm test
```

## Configuración de nginx (Opcional)

Para personalizar la configuración de nginx, crear archivo `nginx.conf`:

```nginx
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    # Configuración para Angular Router
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Configuración de caché para assets estáticos
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

Y descomentar la línea en el Dockerfile:
```dockerfile
COPY nginx.conf /etc/nginx/nginx.conf
```

## Acceso a la Aplicación

Una vez desplegada, la aplicación estará disponible en:

- **Desarrollo**: http://localhost:4200
- **Producción**: http://localhost (o el puerto configurado)

## Características de la Aplicación

- **Login**: Sistema de autenticación con recuperación de contraseña
- **Labs**: Gestión de laboratorios
- **Results**: Visualización de resultados
- **Responsive**: Diseño adaptable con Bootstrap

## Solución de Problemas

### Error de Puerto Ocupado

```bash
# Verificar qué proceso usa el puerto 80
sudo lsof -i :80

# Usar un puerto diferente
docker run -d -p 8080:80 --name angular-labs-app angular-labs
```

### Problemas de Permisos

```bash
# Ejecutar Docker con sudo (no recomendado para producción)
sudo docker run -d -p 80:80 --name angular-labs-app angular-labs
```

### Verificar Estado del Contenedor

```bash
# Inspeccionar contenedor
docker inspect angular-labs-app

# Acceder al shell del contenedor
docker exec -it angular-labs-app sh
```

## Notas de Seguridad

- En producción, considerar usar HTTPS
- Configurar un proxy reverso (nginx, Apache) si es necesario
- Implementar autenticación y autorización adecuadas
- Configurar variables de entorno para diferentes ambientes

## Contacto

Para soporte técnico o consultas sobre el despliegue, contactar al equipo de desarrollo.