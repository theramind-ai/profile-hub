# 🔒 Auditoria de Segurança - ProfileHub

**Data da Auditoria:** 24 de Fevereiro de 2026
**Status:** ⚠️ 12 Vulnerabilidades Identificadas

---

## 🚨 CRÍTICAS (Máxima Prioridade)

### 1. ❌ HARDCODED CREDENTIALS NO DOCKER-COMPOSE
**Severidade:** CRÍTICA  
**Arquivo:** `docker-compose.yml:10, 47, 50`  
**Descrição:** Credenciais de banco de dados e JWT_SECRET expostas em arquivo de controle de versão.

```yaml
# ❌ INSEGURO
environment:
  POSTGRES_PASSWORD: profilehub_password
  SPRING_DATASOURCE_PASSWORD: profilehub_password
  JWT_SECRET: your-super-secret-key-change-this-in-production-environment-with-at-least-32-characters
```

**Impacto:** 
- Acesso não autorizado ao banco de dados
- JWT tokens podem ser forjados
- Violação de credenciais em repositório público

**Correção:** ✅ Será implementada

---

### 2. ❌ HARDCODED CREDENTIALS NO APPLICATION.YML
**Severidade:** CRÍTICA  
**Arquivo:** `backend/src/main/resources/application.yml:8, 33`  
**Descrição:** Credenciais de banco de dados em arquivo de configuração do Spring Boot.

```yaml
# ❌ INSEGURO
datasource:
  username: profilehub_user
  password: profilehub_password
  
app:
  jwtSecret: ${JWT_SECRET:your-super-secret-key-change-this-in-production-environment-with-at-least-32-characters}
```

**Impacto:**
- Build contém credenciais hardcoded
- JAR file expõe credenciais quando descompactado
- Acessível via source code inspection

**Correção:** ✅ Será implementada

---

### 3. ❌ JWT_SECRET FRACO E EXPOSTO NO CÓDIGO PADRÃO
**Severidade:** CRÍTICA  
**Arquivo:** `JwtTokenProvider.java:16`, `application.yml:33`, `docker-compose.yml:50`  
**Descrição:** Chave JWT padrão muito fraca (< 32 caracteres seguro) e incluída em exemplos.

```java
// ❌ INSEGURO
@Value("${app.jwtSecret:your-secret-key-change-this-in-production-environment}")
private String jwtSecret;
```

**Impacto:**
- JWT tokens podem ser quebrados por brute force
- Qualquer pessoa com acesso ao código pode gerar tokens válidos
- Não há rollover de chaves documentado

**Correção:** ✅ Será implementada

---

### 4. ❌ SENHAS NÃO SÃO VALIDADAS EM FORÇA
**Severidade:** ALTA  
**Arquivo:** `backend/src/main/java/com/profilehub/dto/RegisterRequest.java`  
**Descrição:** Validação de senha muito permissiva (apenas minLength(6)).

**Impacto:**
- Senhas fracas como "123456" são aceitas
- Vulnerável a dicionário e força bruta
- Não há requisitos de complexidade

**Correção:** ✅ Será implementada

---

## ⚠️ ALTAS (Alta Prioridade)

### 5. ❌ FALTA DE RATE LIMITING EM ENDPOINTS DE AUTENTICAÇÃO
**Severidade:** ALTA  
**Arquivo:** `AuthController.java:22-27`  
**Descrição:** Sem proteção contra brute force attacks em login/register.

```java
// ❌ INSEGURO - Sem rate limiting
@PostMapping("/login")
public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
    // ...
}
```

**Impacto:**
- Brute force attacks em credenciais
- Enumeração de usuários
- DOS attack possível

**Correção:** ✅ Será implementada

---

### 6. ❌ FALTA DE VALIDAÇÃO DE EXTENSÃO DE ARQUIVO
**Severidade:** ALTA  
**Arquivo:** `FileUtils.java:53-56`  
**Descrição:** Validação por regex apenas - pode ser contornada.

```java
// ⚠️ INCOMPLETO - Apenas regex
public static boolean isAllowedFileType(String fileName) {
    String extension = getFileExtension(fileName).toLowerCase();
    return extension.matches("(pdf|doc|docx|xlsx|xls|ppt|pptx|jpg|jpeg|png|gif|zip|rar|txt)");
}
```

**Impacto:**
- Upload de arquivo malicioso (ex: .exe com nome .txt)
- Possível RCE se servidos diretamente
- Bypass de validação por MIME type falso

**Correção:** ✅ Será implementada

---

### 7. ❌ FALTA VALIDAÇÃO DE MIME TYPE DO ARQUIVO
**Severidade:** ALTA  
**Arquivo:** `DocumentService.java` (uploadDocument)  
**Descrição:** Não valida MIME type real do arquivo.

**Impacto:**
- Arquivo .exe pode ser renomeado para .pdf
- Malware pode ser armazenado
- Sem proteção contra polyglot files

**Correção:** ✅ Será implementada

---

### 8. ❌ CORS MUITO PERMISSIVO
**Severidade:** ALTA  
**Arquivo:** `AuthController.java:15`, `DocumentController.java:20`, `UserController.java:14`  
**Descrição:** CORS com origins hardcoded incluindo localhost inseguro.

```java
// ⚠️ INSEGURO
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
```

**Impacto:**
- Fácil comprometer em produção se deixado assim
- Sem configuração dinâmica
- Hardcoded no código

**Correção:** ✅ Será implementada

---

### 9. ❌ FALTA PROTEÇÃO CONTRA CSRF
**Severidade:** ALTA  
**Arquivo:** `SecurityConfig.java`  
**Descrição:** CSRF desabilitado globalmente sem alternativa.

```java
// ⚠️ INSEGURO
http.csrf((csrf) -> csrf.disable());
```

**Impacto:**
- Ataques CSRF possíveis em aplicação browser-based
- Nenhuma proteção contra forged requests

**Correção:** ✅ Será implementada

---

### 10. ❌ FALTA VALIDAÇÃO DE AUTORIZAÇÃO NO CONTROLLER
**Severidade:** ALTA  
**Arquivo:** `DocumentController.java:46-52` (linha 52 parcial)  
**Descrição:** `updateDocument` não valida se usuário é proprietário do documento.

```java
// ⚠️ INSEGURO - Não verifica ownership
@PutMapping("/{documentId}")
public ResponseEntity<DocumentResponse> updateDocument(
    @PathVariable Long documentId,
    @RequestBody Document documentDetails,
    Authentication authentication
) {
    // Deveria validar se documentId pertence a usuário autenticado
}
```

**Impacto:**
- Horizontal privilege escalation
- Usuário pode modificar documentos de outro usuário
- Acesso não autorizado a dados

**Correção:** ✅ Será implementada

---

## ⚠️ MÉDIAS (Média Prioridade)

### 11. ❌ INFORMAÇÕES SENSÍVEIS EM LOGS
**Severidade:** MÉDIA  
**Arquivo:** `AuthController.java:24, 28`  
**Descrição:** Email de usuários registrados em logs.

```java
log.info("Login attempt for email: {}", loginRequest.getEmail());
log.info("Registration for email: {}", registerRequest.getEmail());
```

**Impacto:**
- PII (Personally Identifiable Information) em logs
- GDPR/LGPD compliance issue
- Log files podem ser compromete

**Correção:** ✅ Será implementada

---

### 12. ❌ FALTA DE LOGGING DE EVENTOS DE SEGURANÇA
**Severidade:** MÉDIA  
**Arquivo:** Projeto inteiro  
**Descrição:** Sem audit trail de operações sensíveis.

**Impacto:**
- Impossível rastrear atividade maliciosa
- Sem conformidade compliance
- Detecção de intrusão limitada

**Correção:** ✅ Será implementada

---

## 📋 GAPS DE CÓDIGO

### Gap 1: Falta Exception Handler para Exceções de Arquivo
**Arquivo:** `GlobalExceptionHandler.java`  
**Descrição:** Sem tratamento para `FileUploadException` ou `FileSizeLimitExceededException`.

```java
// ❌ FALTANDO
@ExceptionHandler(MaxUploadSizeExceededException.class)
public ResponseEntity<ErrorResponse> handleFileSizeException(MaxUploadSizeExceededException ex) {
    // ...
}
```

**Impacto:** Erro genérico 500 em vez de 413 Payload Too Large.

---

### Gap 2: Falta Logging de Operações Sensíveis
**Arquivo:** `DocumentService.java`, `UserService.java`  
**Descrição:** Sem audit trail de delete/update de dados sensíveis.

**Impacto:** Impossível rastrear mudanças de dados.

---

### Gap 3: Falta Validação de Entrada em DTOs
**Arquivo:** `DocumentResponse.java`  
**Descrição:** DTOs não têm validações de tamanho/padrão.

```java
// ❌ FALTANDO @Size, @Pattern
@Data
public class DocumentResponse {
    private Long id;
    private String title;  // Sem @NotBlank ou @Size
    private String description;  // Sem @Size
}
```

---

### Gap 4: Falta Proteção Contra Path Traversal
**Arquivo:** `FileUtils.java:17-22`  
**Descrição:** Salva arquivo com nome original sem sanitizar path traversal.

```java
// ⚠️ VULNERÁVEL
String uploadPath = UPLOAD_DIR + File.separator + UUID.randomUUID() + "_" + originalFileName;
// Se originalFileName = "../../etc/passwd", pode causar path traversal
```

---

### Gap 5: Falta Encryption de Dados Sensíveis
**Arquivo:** Projeto inteiro  
**Descrição:** Senhas armazenadas, mas sem encryption adicional.

**Impacto:** Se banco for comprometido, dados sensíveis ficam expostos.

---

### Gap 6: Falta HTTPS/TLS Enforcement
**Arquivo:** `SecurityConfig.java`, `docker-compose.yml`  
**Descrição:** Sem força de HTTPS no Spring Security.

```java
// ❌ FALTANDO
http.requiresChannel().anyRequest().requiresSecure();
```

---

### Gap 7: Falta Validação de Token Expirado
**Arquivo:** `JwtAuthenticationFilter.java`  
**Descrição:** Possível que token expirado não seja rejeitado corretamente.

**Impacto:** Sessões podem ser prolongadas indefinidamente.

---

### Gap 8: Falta Health Check Authentication
**Arquivo:** `AuthController.java:31-33`  
**Descrição:** Endpoint `/health` sem autenticação (pode ser explorado).

```java
// ⚠️ PÚBLICO
@GetMapping("/health")
public ResponseEntity<String> health() {
    return ResponseEntity.ok("Auth service is running");
}
```

---

---

## 📊 RESUMO DE SEVERIDADE

| Severidade | Quantidade | Status |
|-----------|-----------|--------|
| 🔴 CRÍTICA | 4 | ⚠️ Precisa Correção |
| 🟠 ALTA | 6 | ⚠️ Precisa Correção |
| 🟡 MÉDIA | 2 | ⚠️ Precisa Correção |
| **TOTAL** | **12** | **⚠️ 100% para Corrigir** |

---

## ✅ CHECKLIST DE REMEDIAÇÃO

### Fase 1: CRÍTICAS (Urgente)
- [ ] Remover credenciais do docker-compose.yml
- [ ] Remover credenciais do application.yml
- [ ] Gerar JWT_SECRET forte (64+ caracteres)
- [ ] Implementar .env com variáveis seguras

### Fase 2: ALTAS (Importante)
- [ ] Implementar Rate Limiting em auth endpoints
- [ ] Validar MIME type de arquivos
- [ ] Sanitizar path traversal em upload
- [ ] Corrigir CORS configuration
- [ ] Implementar CSRF protection
- [ ] Adicionar authorization checks em updates

### Fase 3: MÉDIAS (Importante)
- [ ] Remover PII de logs
- [ ] Implementar audit trail
- [ ] Adicionar security event logging

### Fase 4: GAPS (Otimização)
- [ ] Exception handlers completos
- [ ] Validações em DTOs
- [ ] Encryption de dados sensíveis
- [ ] HTTPS enforcement
- [ ] Token expiration checks

---

## 📚 REFERÊNCIAS OWASP

- [OWASP Top 10 2023](https://owasp.org/Top10/)
- [A01: Broken Access Control](https://owasp.org/Top10/A01_2021-Broken_Access_Control/)
- [A02: Cryptographic Failures](https://owasp.org/Top10/A02_2021-Cryptographic_Failures/)
- [A03: Injection](https://owasp.org/Top10/A03_2021-Injection/)
- [A07: Identification and Authentication Failures](https://owasp.org/Top10/A07_2021-Identification_and_Authentication_Failures/)

---

**Próxima Ação:** Implementar todas as correções de segurança listadas acima.

