# ProfileHub - Aplicação Fullstack Java + Angular

Bem-vindo ao **ProfileHub**! Uma aplicação moderna fullstack que combina Spring Boot no backend com Angular no frontend, oferecendo autenticação segura com JWT, gerenciamento de usuários e upload de documentos.

## 📋 Índice

- [Visão Geral](#visão-geral)
- [Arquitetura](#arquitetura)
- [Pré-requisitos](#pré-requisitos)
- [Instalação Local](#instalação-local)
- [Usando Docker](#usando-docker)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [API Endpoints](#api-endpoints)
- [Exemplos de Payloads](#exemplos-de-payloads)
- [Variáveis de Ambiente](#variáveis-de-ambiente)
- [Troubleshooting](#troubleshooting)

## 🎯 Visão Geral

ProfileHub é uma plataforma que permite:

- ✅ Registro e autenticação segura de usuários
- ✅ Gerenciamento de perfil de usuário
- ✅ Upload e gerenciamento de documentos
- ✅ Compartilhamento de documentos públicos
- ✅ Sistema de controle de acesso baseado em papéis (RBAC)
- ✅ Cache distribuído com Redis
- ✅ Banco de dados PostgreSQL

## 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas com separação clara entre frontend, backend e infraestrutura.

## 📋 Pré-requisitos

### Para execução local:
- Java 21 JDK
- Maven 3.9+
- Node.js 20+
- npm ou yarn
- PostgreSQL 13+
- Redis 7+

### Para execução com Docker:
- Docker 20.10+
- Docker Compose 2.0+

## 🚀 Instalação Local

### 1. Backend - Spring Boot

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### 2. Frontend - Angular

```bash
cd frontend
npm install
npm start
```

## 🐳 Usando Docker

```bash
docker-compose up
```

## 🔌 API Endpoints

### Autenticação
- `POST /api/auth/login` - Fazer login
- `POST /api/auth/register` - Registrar novo usuário

### Usuários
- `GET /api/users/me` - Obter usuário atual
- `PUT /api/users/{userId}` - Atualizar usuário

### Documentos
- `POST /api/documents/upload` - Upload de documento
- `GET /api/documents/user/documents` - Listar documentos do usuário
- `DELETE /api/documents/{documentId}` - Deletar documento

## 🌍 Variáveis de Ambiente

Configure em `backend/src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/profilehub
    username: profilehub_user
    password: profilehub_password
```

## 📚 Documentação Completa

Para documentação detalhada, veja os arquivos específicos de cada módulo.

---

**Desenvolvido com ❤️ por Igor Melo**
A place where user can register your profile-cv for to have this in web every time that he needs. 
