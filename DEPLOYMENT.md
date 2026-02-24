# Guia de Implantação - ProfileHub

## 📋 Sumário

- [Pré-requisitos de Produção](#pré-requisitos-de-produção)
- [Implantação com Docker](#implantação-com-docker)
- [Configurações de Segurança](#configurações-de-segurança)
- [Otimizações de Performance](#otimizações-de-performance)
- [Monitoramento e Logs](#monitoramento-e-logs)
- [Troubleshooting de Produção](#troubleshooting-de-produção)

## 🔒 Pré-requisitos de Produção

### Infraestrutura
- Servidor Linux (Ubuntu 22.04 LTS recomendado)
- Docker Engine 20.10+
- Docker Compose 2.0+
- Mínimo 2GB RAM
- Mínimo 20GB SSD

### Segurança
- SSL/TLS Certificate (Let's Encrypt)
- Firewall configurado
- SSH Key-based authentication

## 🚀 Implantação com Docker

### 1. Preparar Servidor

```bash
# Atualizar sistema
sudo apt update && sudo apt upgrade -y

# Instalar Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Instalar Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Adicionar usuário ao grupo docker
sudo usermod -aG docker $USER
newgrp docker
```

### 2. Clonar Repositório

```bash
cd /opt
sudo git clone <repository-url> profile-hub
cd profile-hub
sudo chown -R $USER:$USER .
```

### 3. Configurar Variáveis de Ambiente

```bash
# Copiar arquivo de exemplo
cp .env.example .env

# Editar arquivo com valores de produção
nano .env
```

**Valores importantes para produção:**
```env
JWT_SECRET=seu-chave-super-secreta-muito-longa-aqui-min-32-chars
SPRING_DATASOURCE_PASSWORD=senha-super-segura-aqui
SPRING_REDIS_PASSWORD=senha-redis-aqui
```

### 4. Configurar Docker Compose para Produção

Criar `docker-compose.prod.yml`:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16-alpine
    container_name: profilehub-postgres
    restart: always
    environment:
      POSTGRES_DB: profilehub
      POSTGRES_USER: profilehub_user
      POSTGRES_PASSWORD: ${SPRING_DATASOURCE_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./postgres/init.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - profilehub-network
    ports:
      - "127.0.0.1:5432:5432"

  redis:
    image: redis:7-alpine
    container_name: profilehub-redis
    restart: always
    command: redis-server --requirepass ${SPRING_REDIS_PASSWORD}
    volumes:
      - redis_data:/data
    networks:
      - profilehub-network
    ports:
      - "127.0.0.1:6379:6379"

  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: profilehub-backend
    restart: always
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/profilehub
      SPRING_DATASOURCE_USERNAME: profilehub_user
      SPRING_DATASOURCE_PASSWORD: ${SPRING_DATASOURCE_PASSWORD}
      SPRING_REDIS_HOST: redis
      SPRING_REDIS_PORT: 6379
      SPRING_REDIS_PASSWORD: ${SPRING_REDIS_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      SPRING_PROFILES_ACTIVE: prod
    ports:
      - "127.0.0.1:8080:8080"
    depends_on:
      - postgres
      - redis
    networks:
      - profilehub-network
    volumes:
      - ./backend/uploads:/app/uploads

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    container_name: profilehub-frontend
    restart: always
    ports:
      - "127.0.0.1:3000:80"
    depends_on:
      - backend
    networks:
      - profilehub-network

  nginx:
    image: nginx:alpine
    container_name: profilehub-nginx
    restart: always
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.prod.conf:/etc/nginx/nginx.conf
      - /etc/letsencrypt:/etc/letsencrypt
      - /var/www/certbot:/var/www/certbot
    depends_on:
      - frontend
      - backend
    networks:
      - profilehub-network

volumes:
  postgres_data:
  redis_data:

networks:
  profilehub-network:
    driver: bridge
```

### 5. Configurar Nginx para Produção

Criar `nginx.prod.conf`:

```nginx
upstream backend {
    server profilehub-backend:8080;
}

upstream frontend {
    server profilehub-frontend:80;
}

server {
    listen 80;
    server_name seu-dominio.com www.seu-dominio.com;

    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
    }

    location / {
        return 301 https://$server_name$request_uri;
    }
}

server {
    listen 443 ssl http2;
    server_name seu-dominio.com www.seu-dominio.com;

    ssl_certificate /etc/letsencrypt/live/seu-dominio.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/seu-dominio.com/privkey.pem;

    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;

    # Gzip compression
    gzip on;
    gzip_types text/plain text/css application/json application/javascript;

    # Frontend
    location / {
        proxy_pass http://frontend;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
    }

    # Backend API
    location /api/ {
        proxy_pass http://backend/api/;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### 6. Configurar SSL/TLS com Let's Encrypt

```bash
# Instalar certbot
sudo apt install certbot python3-certbot-nginx -y

# Gerar certificado
sudo certbot certonly --standalone -d seu-dominio.com -d www.seu-dominio.com

# Configurar renovação automática
sudo systemctl enable certbot.timer
```

### 7. Iniciar Aplicação em Produção

```bash
# Build e start
docker-compose -f docker-compose.prod.yml build
docker-compose -f docker-compose.prod.yml up -d

# Ver logs
docker-compose -f docker-compose.prod.yml logs -f

# Parar
docker-compose -f docker-compose.prod.yml down
```

## 🔒 Configurações de Segurança

### 1. Variáveis de Ambiente Sensíveis

Armazenar em arquivo `.env` com permissões restritas:

```bash
# Apenas owner pode ler
chmod 600 .env
```

### 2. Configurar Firewall

```bash
# UFW (Ubuntu)
sudo ufw allow 22/tcp     # SSH
sudo ufw allow 80/tcp     # HTTP
sudo ufw allow 443/tcp    # HTTPS
sudo ufw enable
```

### 3. Database Security

```bash
# Backup automático
docker-compose exec postgres pg_dump -U profilehub_user profilehub > backup.sql

# Restaurar
docker-compose exec -T postgres psql -U profilehub_user profilehub < backup.sql
```

### 4. Redis Security

```yaml
# docker-compose.yml
redis:
  command: redis-server --requirepass ${REDIS_PASSWORD} --appendonly yes
```

## ⚡ Otimizações de Performance

### 1. Backend

```yaml
# application-prod.yml
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 50
          fetch_size: 50
        format_sql: false
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
```

### 2. Frontend

```bash
# Build otimizado
npm run build:prod

# Comprimir assets
gzip -r dist/
```

### 3. Redis Caching

```bash
# Configurar persistência
appendonly yes
appendfsync everysec
```

## 📊 Monitoramento e Logs

### 1. Logs Centralizados

```bash
# Ver logs em tempo real
docker-compose -f docker-compose.prod.yml logs -f backend

# Ver logs de container específico
docker logs -f profilehub-backend

# Salvar logs
docker-compose -f docker-compose.prod.yml logs backend > backend.log
```

### 2. Healthcheck

```bash
# Verificar saúde da aplicação
curl https://seu-dominio.com/api/health

# Response esperado:
# {"status": "UP"}
```

### 3. Monitoramento de Recursos

```bash
# CPU e memória
docker stats

# Disk usage
df -h

# Memory usage
free -h
```

## 🐛 Troubleshooting de Produção

### Problema: Aplicação não está iniciando

```bash
# Verificar logs
docker-compose -f docker-compose.prod.yml logs

# Verificar variáveis de ambiente
docker-compose config

# Rebuiidar imagens
docker-compose -f docker-compose.prod.yml build --no-cache
```

### Problema: Database connection error

```bash
# Verificar conectividade
docker-compose exec postgres psql -U profilehub_user -d profilehub -c "SELECT 1;"

# Verificar credenciais
grep DATASOURCE .env
```

### Problema: Redis connection timeout

```bash
# Verificar Redis
docker-compose exec redis redis-cli ping

# Aumentar timeout
SPRING_REDIS_TIMEOUT=5000
```

### Problema: Disk space full

```bash
# Limpar Docker
docker system prune -a

# Limpar volumes não usados
docker volume prune

# Usar df para monitorar
df -h /var/lib/docker
```

## 🔄 Backup e Recovery

### Backup Completo

```bash
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)

# Backup database
docker-compose exec -T postgres pg_dump -U profilehub_user profilehub > backup_db_$DATE.sql

# Backup volumes
tar -czf backup_volumes_$DATE.tar.gz postgres_data/ redis_data/ backend/uploads/

# Backup configs
tar -czf backup_configs_$DATE.tar.gz .env nginx.prod.conf

# Upload to cloud storage (S3, etc)
aws s3 cp backup_db_$DATE.sql s3://seu-bucket/backups/
aws s3 cp backup_volumes_$DATE.tar.gz s3://seu-bucket/backups/
aws s3 cp backup_configs_$DATE.tar.gz s3://seu-bucket/backups/
```

### Recovery

```bash
# Restaurar database
docker-compose exec -T postgres psql -U profilehub_user profilehub < backup_db_DATE.sql

# Restaurar volumes
tar -xzf backup_volumes_DATE.tar.gz

# Restaurar configs
tar -xzf backup_configs_DATE.tar.gz
```

---

**Última atualização:** Fevereiro 2026
