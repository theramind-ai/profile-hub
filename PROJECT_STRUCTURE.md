PROJECT_STRUCTURE.md

# 📁 Estrutura Completa do ProfileHub

## 🌳 Árvore de Diretórios

```
profile-hub/
│
├── 📄 README.md                    # Documentação principal
├── 📄 DEPLOYMENT.md                # Guia de implantação em produção
├── 📄 API_EXAMPLES.http            # Exemplos de requisições HTTP
├── 📄 docker-compose.yml           # Orquestração de containers
├── 📄 .gitignore                   # Arquivos ignorados por Git
├── 📄 .env.example                 # Variáveis de ambiente (exemplo)
├── 📄 setup.sh                     # Script de setup inicial
│
├── 📦 backend/                     # Spring Boot Backend
│   ├── 📄 pom.xml                  # Maven configuration
│   ├── 📄 Dockerfile               # Docker image para backend
│   ├── 📄 .dockerignore            # Arquivos ignorados no Docker
│   │
│   └── src/main/java/com/profilehub/
│       │
│       ├── ProfileHubApplication.java              # Classe principal
│       │
│       ├── 📁 config/                              # Configurações
│       │   ├── SecurityConfig.java                 # Spring Security
│       │   ├── CorsConfig.java                     # CORS configuration
│       │   └── JwtConfig.java                      # JWT settings
│       │
│       ├── 📁 controller/                          # REST Controllers
│       │   ├── AuthController.java                 # Autenticação
│       │   ├── UserController.java                 # Usuários
│       │   └── DocumentController.java             # Documentos
│       │
│       ├── 📁 service/                             # Lógica de Negócio
│       │   ├── AuthService.java                    # Autenticação
│       │   ├── UserService.java                    # Usuários
│       │   └── DocumentService.java                # Documentos
│       │
│       ├── 📁 repository/                          # Data Access
│       │   ├── UserRepository.java                 # JPA para User
│       │   └── DocumentRepository.java             # JPA para Document
│       │
│       ├── 📁 entity/                              # Modelos JPA
│       │   ├── User.java                           # Entidade User
│       │   ├── Document.java                       # Entidade Document
│       │   └── Role.java                           # Enum de Roles
│       │
│       ├── 📁 dto/                                 # Data Transfer Objects
│       │   ├── LoginRequest.java
│       │   ├── RegisterRequest.java
│       │   ├── UserResponse.java
│       │   ├── AuthResponse.java
│       │   └── DocumentResponse.java
│       │
│       ├── 📁 security/                            # JWT & Segurança
│       │   ├── JwtTokenProvider.java               # Geração de JWT
│       │   ├── JwtAuthenticationFilter.java        # Filtro JWT
│       │   ├── UserDetailsImpl.java                 # User Details
│       │   └── UserDetailsServiceImpl.java          # User Details Service
│       │
│       ├── 📁 exception/                           # Tratamento de Erros
│       │   ├── ApiException.java                   # Exception customizada
│       │   ├── ErrorResponse.java                  # Response de erro
│       │   └── GlobalExceptionHandler.java         # Exception Handler global
│       │
│       ├── 📁 util/                                # Utilitários
│       │   └── FileUtils.java                      # Utilidades de arquivo
│       │
│       └── resources/
│           └── application.yml                     # Configurações da aplicação
│
├── 📦 frontend/                    # Angular Frontend
│   ├── 📄 package.json             # Dependências npm
│   ├── 📄 angular.json             # Configuração Angular
│   ├── 📄 tsconfig.json            # TypeScript config
│   ├── 📄 tsconfig.app.json        # TypeScript app config
│   ├── 📄 tsconfig.spec.json       # TypeScript specs config
│   ├── 📄 Dockerfile               # Docker image para frontend
│   ├── 📄 nginx.conf               # Nginx configuration
│   ├── 📄 .dockerignore            # Arquivos ignorados no Docker
│   │
│   └── src/
│       ├── 📄 index.html           # HTML principal
│       ├── 📄 main.ts              # Entry point
│       ├── 📄 styles.css           # Estilos globais
│       │
│       └── app/
│           ├── 📄 app.component.ts         # Root component
│           ├── 📄 app.config.ts           # Configuração da app
│           ├── 📄 app.routes.ts           # Rotas
│           │
│           ├── 📁 services/                # HTTP Services
│           │   ├── auth.service.ts        # Autenticação
│           │   ├── user.service.ts        # Usuários
│           │   ├── document.service.ts    # Documentos
│           │   ├── storage.service.ts     # LocalStorage
│           │   └── http-interceptor.ts    # HTTP Interceptor
│           │
│           ├── 📁 components/             # Componentes Angular
│           │   ├── login/
│           │   │   └── login.component.ts
│           │   ├── register/
│           │   │   └── register.component.ts
│           │   ├── dashboard/
│           │   │   └── dashboard.component.ts
│           │   ├── document-upload/
│           │   │   └── document-upload.component.ts
│           │   ├── navbar/
│           │   │   └── navbar.component.ts
│           │   └── loading-spinner/
│           │       └── spinner.component.ts
│           │
│           └── 📁 guards/                 # Route Guards
│               └── auth.guard.ts         # Autenticação guard
│
└── 📦 postgres/                    # Database
    └── 📄 init.sql                 # Script de inicialização
```

## 📋 Árvore Resumida (Visualização)

```
profile-hub/
├── Backend (Java/Spring Boot)
│   ├── Controllers (3)
│   │   ├── AuthController
│   │   ├── UserController
│   │   └── DocumentController
│   ├── Services (3)
│   │   ├── AuthService
│   │   ├── UserService
│   │   └── DocumentService
│   ├── Repositories (2)
│   │   ├── UserRepository
│   │   └── DocumentRepository
│   ├── Entities (3)
│   │   ├── User
│   │   ├── Document
│   │   └── Role
│   ├── DTOs (5)
│   │   ├── LoginRequest
│   │   ├── RegisterRequest
│   │   ├── UserResponse
│   │   ├── AuthResponse
│   │   └── DocumentResponse
│   ├── Security (4)
│   │   ├── JwtTokenProvider
│   │   ├── JwtAuthenticationFilter
│   │   ├── UserDetailsImpl
│   │   └── UserDetailsServiceImpl
│   ├── Config (2)
│   │   ├── SecurityConfig
│   │   └── CorsConfig
│   ├── Exception (3)
│   │   ├── ApiException
│   │   ├── ErrorResponse
│   │   └── GlobalExceptionHandler
│   └── Utils (1)
│       └── FileUtils
│
├── Frontend (Angular 17)
│   ├── Components (5)
│   │   ├── Login
│   │   ├── Register
│   │   ├── Dashboard
│   │   ├── DocumentUpload
│   │   └── Navbar
│   ├── Services (5)
│   │   ├── AuthService
│   │   ├── UserService
│   │   ├── DocumentService
│   │   ├── StorageService
│   │   └── HttpInterceptor
│   ├── Guards (1)
│   │   └── AuthGuard
│   └── Routes
│       ├── /login
│       ├── /register
│       ├── /dashboard
│       └── /upload
│
├── Infrastructure
│   ├── Docker Compose (1 arquivo)
│   ├── PostgreSQL (1 container)
│   ├── Redis (1 container)
│   ├── Backend Container (1)
│   └── Frontend Container (1)
│
└── Documentation
    ├── README.md
    ├── DEPLOYMENT.md
    ├── API_EXAMPLES.http
    └── .env.example
```

## 🔌 Endpoints API

### Autenticação (2 endpoints)
- `POST /api/auth/login`
- `POST /api/auth/register`

### Usuários (5 endpoints)
- `GET /api/users/me`
- `GET /api/users/{userId}`
- `PUT /api/users/{userId}`
- `POST /api/users/{userId}/change-password`
- `DELETE /api/users/{userId}`

### Documentos (7 endpoints)
- `POST /api/documents/upload`
- `GET /api/documents/{documentId}`
- `GET /api/documents/user/documents`
- `GET /api/documents/user/documents/paginated`
- `GET /api/documents/public`
- `PUT /api/documents/{documentId}`
- `DELETE /api/documents/{documentId}`

**Total: 14 endpoints REST**

## 📊 Estatísticas do Projeto

### Backend (Java/Spring Boot)
- Controllers: 3
- Services: 3
- Repositories: 2
- Entities: 3
- DTOs: 5
- Security Classes: 4
- Config Classes: 2
- Exception Classes: 3
- Utility Classes: 1
- **Total de classes: 26+**

### Frontend (Angular)
- Components: 5+
- Services: 5
- Guards: 1
- Routes: 4+
- **Total de arquivos TypeScript: 15+**

### Infrastructure
- Containers: 5 (Frontend, Backend, PostgreSQL, Redis, Nginx)
- Volumes: 2 (Database, Redis)
- Networks: 1

### Arquivos de Configuração
- Docker files: 3 (docker-compose.yml + 2 Dockerfiles)
- Configuration files: 10+
- Documentation: 4 files
- Total: 17+ arquivos de configuração/documentação

## 🎯 Características Implementadas

### Backend
✅ Autenticação com JWT
✅ Spring Security
✅ JPA/Hibernate
✅ Validação de dados
✅ Tratamento de exceções
✅ CORS configurado
✅ Upload de arquivos
✅ Paginação
✅ Redis caching
✅ PostgreSQL

### Frontend
✅ Componentes standalone
✅ Reactive forms
✅ Guards de rota
✅ HTTP Interceptor
✅ LocalStorage
✅ Validação de formulário
✅ Responsive design
✅ Error handling
✅ Loading states
✅ TypeScript strict mode

### DevOps
✅ Docker Compose
✅ Multi-stage Dockerfile
✅ PostgreSQL init script
✅ Nginx reverse proxy
✅ Health checks
✅ Volume management
✅ Network isolation

## 🚀 Como Usar Este Projeto

### 1. Instalação Local
```bash
./setup.sh
```

### 2. Com Docker
```bash
docker-compose up
```

### 3. Acessar
- Frontend: http://localhost (ou 4200 em dev)
- Backend API: http://localhost:8080/api

### 4. Testar API
Use arquivo `API_EXAMPLES.http` ou Postman

## 📚 Documentação Disponível

| Arquivo | Conteúdo |
|---------|----------|
| README.md | Guia principal e instruções |
| DEPLOYMENT.md | Guia de produção e segurança |
| API_EXAMPLES.http | Exemplos de requisições |
| .env.example | Variáveis de ambiente |
| setup.sh | Script de inicialização |
| docker-compose.yml | Orquestração Docker |

---

**Projeto ProfileHub - Fullstack Java + Angular 2026**
