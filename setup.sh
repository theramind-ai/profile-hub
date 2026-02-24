#!/bin/bash

# ProfileHub - Script de Inicialização Local

echo "====================================="
echo "ProfileHub - Setup Inicial"
echo "====================================="
echo ""

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Verificar pré-requisitos
echo -e "${YELLOW}1. Verificando pré-requisitos...${NC}"

# Verificar Java
if ! command -v java &> /dev/null; then
    echo -e "${RED}Java não encontrado. Por favor, instale Java 21.${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Java encontrado$(java -version 2>&1 | grep version)${NC}"

# Verificar Maven
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}Maven não encontrado. Por favor, instale Maven.${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Maven encontrado$(mvn -v 2>&1 | grep version)${NC}"

# Verificar Node.js
if ! command -v node &> /dev/null; then
    echo -e "${RED}Node.js não encontrado. Por favor, instale Node.js 20+.${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Node.js encontrado: $(node -v)${NC}"

# Verificar PostgreSQL
if ! command -v psql &> /dev/null; then
    echo -e "${RED}PostgreSQL não encontrado. Por favor, instale PostgreSQL.${NC}"
    exit 1
fi
echo -e "${GREEN}✓ PostgreSQL encontrado${NC}"

# Verificar Redis
if ! command -v redis-cli &> /dev/null; then
    echo -e "${RED}Redis não encontrado. Por favor, instale Redis.${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Redis encontrado${NC}"

echo ""
echo -e "${YELLOW}2. Configurando Backend...${NC}"

# Build Backend
cd backend
echo "Instalando dependências Maven..."
mvn clean install -q

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Backend configurado com sucesso${NC}"
else
    echo -e "${RED}✗ Erro ao configurar backend${NC}"
    exit 1
fi

cd ..

echo ""
echo -e "${YELLOW}3. Configurando Frontend...${NC}"

cd frontend
echo "Instalando dependências npm..."
npm install > /dev/null 2>&1

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Frontend configurado com sucesso${NC}"
else
    echo -e "${RED}✗ Erro ao configurar frontend${NC}"
    exit 1
fi

cd ..

echo ""
echo -e "${YELLOW}4. Configurando Banco de Dados...${NC}"

# Criar banco de dados
echo "Criando banco de dados ProfileHub..."
psql -U postgres -c "DROP DATABASE IF EXISTS profilehub;" 2>/dev/null
psql -U postgres -c "CREATE DATABASE profilehub;" 2>/dev/null
psql -U postgres -c "CREATE USER profilehub_user WITH PASSWORD 'profilehub_password';" 2>/dev/null
psql -U postgres -c "ALTER USER profilehub_user WITH SUPERUSER;" 2>/dev/null

# Executar script de inicialização
psql -U profilehub_user -d profilehub -f postgres/init.sql > /dev/null 2>&1

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Banco de dados configurado com sucesso${NC}"
else
    echo -e "${RED}✗ Erro ao configurar banco de dados${NC}"
fi

echo ""
echo -e "${GREEN}====================================="
echo "Setup Concluído com Sucesso!"
echo "=====================================${NC}"
echo ""
echo "Para iniciar a aplicação, abra dois terminais:"
echo ""
echo "Terminal 1 - Backend:"
echo "  cd backend"
echo "  mvn spring-boot:run"
echo ""
echo "Terminal 2 - Frontend:"
echo "  cd frontend"
echo "  npm start"
echo ""
echo "Aplicação estará disponível em:"
echo "  Frontend: http://localhost:4200"
echo "  Backend:  http://localhost:8080"
echo "  API:      http://localhost:8080/api"
echo ""
