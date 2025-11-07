#################################################################################
# FinanceHub - Complete Docker Generator Script
# Author: @tekteku
# Date: 2025-11-07 08:33:34 UTC
# Description: Generates all Docker-related files for FinanceHub
#################################################################################

Write-Host "🐳 FinanceHub Docker Generator" -ForegroundColor Cyan
Write-Host "==============================" -ForegroundColor Cyan
Write-Host ""

$projectRoot = $PSScriptRoot

# Create directory structure
Write-Host "📁 Creating Docker directory structure..." -ForegroundColor Yellow

$directories = @(
    "docker\postgres\init",
    "docker\nginx\conf.d",
    "docker\prometheus",
    "docker\grafana\provisioning\dashboards",
    "docker\grafana\provisioning\datasources",
    "ssl",
    "logs\nginx",
    "data\postgres",
    "data\redis"
)

foreach ($dir in $directories) {
    $fullPath = Join-Path $projectRoot $dir
    New-Item -ItemType Directory -Path $fullPath -Force | Out-Null
}

Write-Host "✅ Directory structure created!" -ForegroundColor Green

#################################################################################
# BACKEND DOCKERFILES
#################################################################################

Write-Host "🔨 Creating backend Dockerfiles..." -ForegroundColor Yellow

# backend/Dockerfile
@"
# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
"@ | Out-File -FilePath (Join-Path $projectRoot "backend\Dockerfile") -Encoding UTF8

# backend/Dockerfile.prod
@"
# Multi-stage build for production
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY src ./src

# Build application with production profile
RUN mvn clean package -DskipTests -P production

# Production stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Install dependencies
RUN apk add --no-cache curl bash

# Copy built jar
COPY --from=build /app/target/*.jar app.jar

# Create logs directory
RUN mkdir -p /app/logs

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring && \
    chown -R spring:spring /app
USER spring:spring

EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM optimization for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0"

ENTRYPOINT ["sh", "-c", "java `$JAVA_OPTS -jar app.jar"]
"@ | Out-File -FilePath (Join-Path $projectRoot "backend\Dockerfile.prod") -Encoding UTF8

# backend/.dockerignore
@"
target/
!.mvn/wrapper/maven-wrapper.jar
!**/src/main/**/target/
!**/src/test/**/target/

### STS ###
.apt_generated
.classpath
.factorypath
.project
.settings
.springBeans
.sts4-cache

### IntelliJ IDEA ###
.idea
*.iws
*.iml
*.ipr

### NetBeans ###
/nbproject/private/
/nbbuild/
/dist/
/nbdist/
/.nb-gradle/
build/

### VS Code ###
.vscode/

### Logs ###
*.log
logs/

### OS ###
.DS_Store
Thumbs.db
"@ | Out-File -FilePath (Join-Path $projectRoot "backend\.dockerignore") -Encoding UTF8

Write-Host "✅ Backend Dockerfiles created!" -ForegroundColor Green

#################################################################################
# FRONTEND DOCKERFILES
#################################################################################

Write-Host "🎨 Creating frontend Dockerfiles..." -ForegroundColor Yellow

# frontend/Dockerfile
@"
# Build stage
FROM node:20-alpine AS build
WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm ci --prefer-offline --no-audit

# Copy source and build
COPY . .
RUN npm run build -- --configuration=development

# Production stage
FROM nginx:alpine
WORKDIR /usr/share/nginx/html

# Remove default nginx files
RUN rm -rf ./*

# Copy built app
COPY --from=build /app/dist/frontend/browser .

# Copy nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Health check endpoint
RUN echo `'<!DOCTYPE html><html><body>OK</body></html>`' > /usr/share/nginx/html/health

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
"@ | Out-File -FilePath (Join-Path $projectRoot "frontend\Dockerfile") -Encoding UTF8

# frontend/Dockerfile.prod
@"
# Build stage
FROM node:20-alpine AS build
WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm ci --only=production --prefer-offline --no-audit

# Copy source
COPY . .

# Build for production
ARG CONFIGURATION=production
RUN npm run build -- --configuration=`$CONFIGURATION --optimization --build-optimizer

# Production stage with nginx
FROM nginx:alpine
WORKDIR /usr/share/nginx/html

# Install curl for health checks
RUN apk add --no-cache curl

# Remove default nginx files
RUN rm -rf ./*

# Copy built application
COPY --from=build /app/dist/frontend/browser .

# Copy nginx configuration
COPY nginx.prod.conf /etc/nginx/conf.d/default.conf

# Create health check endpoint
RUN echo `'<!DOCTYPE html><html><body>OK</body></html>`' > /usr/share/nginx/html/health

# Non-root user
RUN chown -R nginx:nginx /usr/share/nginx/html && \
    chown -R nginx:nginx /var/cache/nginx && \
    chown -R nginx:nginx /var/log/nginx && \
    chown -R nginx:nginx /etc/nginx/conf.d
RUN touch /var/run/nginx.pid && \
    chown -R nginx:nginx /var/run/nginx.pid

USER nginx

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:80/health || exit 1

CMD ["nginx", "-g", "daemon off;"]
"@ | Out-File -FilePath (Join-Path $projectRoot "frontend\Dockerfile.prod") -Encoding UTF8

# frontend/.dockerignore
@"
node_modules/
dist/
.angular/
coverage/
.vscode/
.idea/
*.log
npm-debug.log*
.DS_Store
Thumbs.db
*.map
.env
.env.local
.env.*.local
"@ | Out-File -FilePath (Join-Path $projectRoot "frontend\.dockerignore") -Encoding UTF8

# frontend/nginx.conf
@"
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    # Gzip compression
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css text/xml text/javascript application/x-javascript application/javascript application/xml+rss application/json;

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "no-referrer-when-downgrade" always;

    location / {
        try_files `$uri `$uri/ /index.html;
    }

    location /api {
        proxy_pass http://backend:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade `$http_upgrade;
        proxy_set_header Connection `'upgrade`';
        proxy_set_header Host `$host;
        proxy_cache_bypass `$http_upgrade;
        proxy_set_header X-Real-IP `$remote_addr;
        proxy_set_header X-Forwarded-For `$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto `$scheme;
    }

    location /health {
        access_log off;
        return 200 "OK\n";
        add_header Content-Type text/plain;
    }

    # Cache static assets
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)`$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    error_page 404 /index.html;
}
"@ | Out-File -FilePath (Join-Path $projectRoot "frontend\nginx.conf") -Encoding UTF8

Write-Host "✅ Frontend Dockerfiles created!" -ForegroundColor Green

#################################################################################
# DOCKER COMPOSE FILES
#################################################################################

Write-Host "📦 Creating docker-compose files..." -ForegroundColor Yellow

# docker-compose.yml (already exists, skip)
# docker-compose.prod.yml (already exists, skip)

#################################################################################
# POSTGRES INIT SCRIPT
#################################################################################

Write-Host "🗄️ Creating PostgreSQL init script..." -ForegroundColor Yellow

@"
-- FinanceHub Database Initialization Script
-- Author: @tekteku
-- Date: 2025-11-07 08:33:34 UTC

-- Enable required extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- Create schemas
CREATE SCHEMA IF NOT EXISTS financehub;

-- Set default schema
SET search_path TO financehub, public;

-- Grant permissions
GRANT ALL PRIVILEGES ON SCHEMA financehub TO admin;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA financehub TO admin;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA financehub TO admin;

-- Create audit trigger function
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS `$`$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
`$`$ language `'plpgsql`';

-- Log initialization
DO `$`$
BEGIN
    RAISE NOTICE `'FinanceHub database initialized successfully!`';
    RAISE NOTICE `'Timestamp: %`', CURRENT_TIMESTAMP;
END`$`$;
"@ | Out-File -FilePath (Join-Path $projectRoot "docker\postgres\init\01-init.sql") -Encoding UTF8

Write-Host "✅ PostgreSQL init script created!" -ForegroundColor Green

#################################################################################
# NGINX CONFIGURATION
#################################################################################

Write-Host "🌐 Creating Nginx configurations..." -ForegroundColor Yellow

# docker/nginx/nginx.conf
@"
user nginx;
worker_processes auto;
error_log /var/log/nginx/error.log warn;
pid /var/run/nginx.pid;

events {
    worker_connections 1024;
    use epoll;
    multi_accept on;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    log_format main `'`$remote_addr - `$remote_user [`$time_local] "`$request" `'
                    `'`$status `$body_bytes_sent "`$http_referer" `'
                    `'"``$http_user_agent" "`$http_x_forwarded_for"`';

    access_log /var/log/nginx/access.log main;

    sendfile on;
    tcp_nopush on;
    tcp_nodelay on;
    keepalive_timeout 65;
    types_hash_max_size 2048;
    client_max_body_size 20M;

    # Gzip Settings
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_comp_level 6;
    gzip_types text/plain text/css text/xml text/javascript application/x-javascript application/javascript application/xml+rss application/json;

    include /etc/nginx/conf.d/*.conf;
}
"@ | Out-File -FilePath (Join-Path $projectRoot "docker\nginx\nginx.conf") -Encoding UTF8

# docker/nginx/conf.d/default.conf
@"
upstream backend_api {
    server backend:8080 max_fails=3 fail_timeout=30s;
    keepalive 32;
}

upstream frontend_app {
    server frontend:80 max_fails=3 fail_timeout=30s;
    keepalive 32;
}

server {
    listen 80;
    server_name localhost financehub.local;

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;

    # API routes
    location /api {
        proxy_pass http://backend_api;
        proxy_http_version 1.1;
        proxy_set_header Upgrade `$http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host `$host;
        proxy_set_header X-Real-IP `$remote_addr;
        proxy_set_header X-Forwarded-For `$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto `$scheme;
        proxy_cache_bypass `$http_upgrade;
        
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # Frontend routes
    location / {
        proxy_pass http://frontend_app;
        proxy_http_version 1.1;
        proxy_set_header Upgrade `$http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host `$host;
        proxy_cache_bypass `$http_upgrade;
    }

    # Health check
    location /health {
        access_log off;
        return 200 "FinanceHub is running\n";
        add_header Content-Type text/plain;
    }
}
"@ | Out-File -FilePath (Join-Path $projectRoot "docker\nginx\conf.d\default.conf") -Encoding UTF8

Write-Host "✅ Nginx configurations created!" -ForegroundColor Green

#################################################################################
# ENVIRONMENT FILES
#################################################################################

Write-Host "🔐 Creating environment files..." -ForegroundColor Yellow

# .env.example
@"
# FinanceHub Environment Variables
# Copy this file to .env and update with your values

# Database Configuration
POSTGRES_USER=admin
POSTGRES_PASSWORD=your_secure_password_here
POSTGRES_DB=financehub

# Redis Configuration
REDIS_PASSWORD=your_redis_password_here

# JWT Configuration
JWT_SECRET=your_very_long_and_secure_jwt_secret_key_minimum_256_bits

# Grafana Configuration
GRAFANA_PASSWORD=your_grafana_password_here

# Application Configuration
SPRING_PROFILES_ACTIVE=production
NODE_ENV=production
"@ | Out-File -FilePath (Join-Path $projectRoot ".env.example") -Encoding UTF8

# .env (development)
@"
# Development Environment Variables
POSTGRES_USER=admin
POSTGRES_PASSWORD=admin
POSTGRES_DB=financehub

REDIS_PASSWORD=financehub123

JWT_SECRET=mySecretKeyForFinanceHubApplicationDevelopmentOnlyNotSecure

GRAFANA_PASSWORD=admin

SPRING_PROFILES_ACTIVE=development
NODE_ENV=development
"@ | Out-File -FilePath (Join-Path $projectRoot ".env") -Encoding UTF8

Write-Host "✅ Environment files created!" -ForegroundColor Green

#################################################################################
# HELPER SCRIPTS
#################################################################################

Write-Host "🛠️ Creating helper scripts..." -ForegroundColor Yellow

# docker-start.ps1
@"
# FinanceHub Docker Start Script
Write-Host "🚀 Starting FinanceHub with Docker Compose..." -ForegroundColor Cyan

# Check if .env exists
if (-not (Test-Path ".env")) {
    Write-Host "⚠️ .env file not found. Creating from .env.example..." -ForegroundColor Yellow
    Copy-Item ".env.example" ".env"
    Write-Host "✅ Please update .env with your configuration" -ForegroundColor Green
    exit
}

# Start services
docker-compose up -d

Write-Host "``n✅ FinanceHub is starting!" -ForegroundColor Green
Write-Host "``n📊 Services:" -ForegroundColor Cyan
Write-Host "  Frontend: http://localhost:4200" -ForegroundColor White
Write-Host "  Backend API: http://localhost:8080/api" -ForegroundColor White
Write-Host "  Swagger Docs: http://localhost:8080/swagger-ui.html" -ForegroundColor White
Write-Host "  PostgreSQL: localhost:5432" -ForegroundColor White
Write-Host "  Redis: localhost:6379" -ForegroundColor White

Write-Host "``n📝 View logs:" -ForegroundColor Cyan
Write-Host "  docker-compose logs -f" -ForegroundColor White
"@ | Out-File -FilePath (Join-Path $projectRoot "docker-start.ps1") -Encoding UTF8

# docker-stop.ps1
@"
Write-Host "🛑 Stopping FinanceHub..." -ForegroundColor Yellow
docker-compose down
Write-Host "✅ FinanceHub stopped!" -ForegroundColor Green
"@ | Out-File -FilePath (Join-Path $projectRoot "docker-stop.ps1") -Encoding UTF8

# docker-build.ps1
@"
Write-Host "🔨 Building FinanceHub Docker images..." -ForegroundColor Cyan
docker-compose build --no-cache
Write-Host "✅ Build complete!" -ForegroundColor Green
"@ | Out-File -FilePath (Join-Path $projectRoot "docker-build.ps1") -Encoding UTF8

# docker-logs.ps1
@"
Write-Host "📋 Viewing FinanceHub logs..." -ForegroundColor Cyan
docker-compose logs -f
"@ | Out-File -FilePath (Join-Path $projectRoot "docker-logs.ps1") -Encoding UTF8

Write-Host "✅ Helper scripts created!" -ForegroundColor Green

#################################################################################
# COMPLETION
#################################################################################

Write-Host ""
Write-Host "🎉 Docker setup generation complete!" -ForegroundColor Green
Write-Host ""
Write-Host "📁 Created files:" -ForegroundColor Cyan
Write-Host "  ✓ backend/Dockerfile" -ForegroundColor White
Write-Host "  ✓ backend/Dockerfile.prod" -ForegroundColor White
Write-Host "  ✓ frontend/Dockerfile" -ForegroundColor White
Write-Host "  ✓ frontend/Dockerfile.prod" -ForegroundColor White
Write-Host "  ✓ docker/postgres/init/01-init.sql" -ForegroundColor White
Write-Host "  ✓ docker/nginx/nginx.conf" -ForegroundColor White
Write-Host "  ✓ docker/nginx/conf.d/default.conf" -ForegroundColor White
Write-Host "  ✓ .env.example" -ForegroundColor White
Write-Host "  ✓ .env" -ForegroundColor White
Write-Host "  ✓ docker-start.ps1" -ForegroundColor White
Write-Host "  ✓ docker-stop.ps1" -ForegroundColor White
Write-Host "  ✓ docker-build.ps1" -ForegroundColor White
Write-Host "  ✓ docker-logs.ps1" -ForegroundColor White
Write-Host ""
Write-Host "🚀 Next steps:" -ForegroundColor Yellow
Write-Host "  1. Update .env with your credentials" -ForegroundColor White
Write-Host "  2. Run: .\docker-start.ps1" -ForegroundColor White
Write-Host "  3. Access: http://localhost:4200" -ForegroundColor White
Write-Host ""
Write-Host "📖 Documentation:" -ForegroundColor Yellow
Write-Host "  - Development: docker-compose up" -ForegroundColor White
Write-Host "  - Production: docker-compose -f docker-compose.prod.yml up" -ForegroundColor White
Write-Host "  - Logs: docker-compose logs -f" -ForegroundColor White
Write-Host ""
