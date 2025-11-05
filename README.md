# ecommerce-user-service
User Service para sistema de ecommerce.

## Características

- Spring Boot 2.5.7 con Java 11
- Base de datos: PostgreSQL
- Service Discovery: Eureka Client
- Circuit Breaker: Resilience4j para tolerancia a fallos
- Actuator para health checks
- Validaciones: Campos requeridos en usuarios, direcciones, credenciales
- Gestión de tokens de verificación

## Endpoints

### User API
Prefijo: `/user-service`

```
GET    /api/users                        - Listar todos los usuarios
GET    /api/users/{userId}               - Obtener usuario por ID
GET    /api/users/username/{username}    - Obtener usuario por username
POST   /api/users                        - Crear usuario
PUT    /api/users                        - Actualizar usuario
PUT    /api/users/{userId}                - Actualizar usuario por ID
DELETE /api/users/{userId}               - Eliminar usuario
```

### Credential API
Prefijo: `/user-service`

```
GET    /api/credentials                            - Listar todas las credenciales
GET    /api/credentials/{credentialId}             - Obtener credencial por ID
GET    /api/credentials/username/{username}        - Obtener credencial por username
POST   /api/credentials                            - Crear credencial
PUT    /api/credentials                            - Actualizar credencial
PUT    /api/credentials/{credentialId}             - Actualizar credencial por ID
DELETE /api/credentials/{credentialId}             - Eliminar credencial
```

### Address API
Prefijo: `/user-service`

```
GET    /api/addresses              - Listar todas las direcciones
GET    /api/addresses/{addressId}  - Obtener dirección por ID
POST   /api/addresses              - Crear dirección
PUT    /api/addresses              - Actualizar dirección
PUT    /api/addresses/{addressId}  - Actualizar dirección por ID
DELETE /api/addresses/{addressId} - Eliminar dirección
```

### Verification Token API
Prefijo: `/user-service`

```
GET    /api/verification-tokens                      - Listar todos los tokens
GET    /api/verification-tokens/{verificationTokenId}  - Obtener token por ID
POST   /api/verification-tokens                       - Crear token de verificación
PUT    /api/verification-tokens                       - Actualizar token
PUT    /api/verification-tokens/{verificationTokenId}  - Actualizar token por ID
DELETE /api/verification-tokens/{verificationTokenId}  - Eliminar token
```

## Testing

### Unit Tests (14+)
- UserServiceImplTest: Tests de lógica de negocio de usuarios
- UserResourceTest: Tests de endpoints REST
- CredentialServiceImplTest: Tests de gestión de credenciales
- AddressServiceImplTest: Tests de gestión de direcciones
- VerificationTokenServiceImplTest: Tests de tokens de verificación
- UserMappingHelperTest: Tests de mapeo de entidades
- CredentialMappingHelperTest: Tests de mapeo de credenciales
- AddressMappingHelperTest: Tests de mapeo de direcciones
- VerificationTokenMappingHelperTest: Tests de mapeo de tokens
- ApiExceptionHandlerTest: Tests de manejo de excepciones

### Integration Tests
- UserResourceTest: Tests de integración de endpoints REST
- Validación de comunicación con base de datos
- Validación de persistencia de datos

**Total: 14+ tests - Todos pasando**

```bash
./mvnw test
```

## Ejecutar

```bash
# Opción 1: Directamente
./mvnw spring-boot:run

# Opción 2: Compilar y ejecutar
./mvnw clean package
java -jar target/user-service-v0.1.0.jar
```

Service corre en: `http://localhost:8083/user-service`

## Configuración

### Circuit Breaker (Resilience4j)

El servicio está configurado con circuit breaker para tolerancia a fallos:

- Failure rate threshold: 50%
- Minimum number of calls: 5
- Sliding window size: 10
- Wait duration in open state: 5s
- Sliding window type: COUNT_BASED

### Service Discovery

El servicio se registra automáticamente en Eureka Server con el nombre `USER-SERVICE`.

### Health Checks

El servicio expone endpoints de health check a través de Spring Boot Actuator:

```
GET /user-service/actuator/health
```

## Funcionalidades Implementadas

- Gestión completa de usuarios (CRUD)
- Gestión de credenciales de autenticación
- Gestión de direcciones de usuarios
- Gestión de tokens de verificación
- Búsqueda de usuarios por username
- Validaciones de campos requeridos
- Manejo de excepciones personalizado
- Circuit breaker para resiliencia
- Integración con Service Discovery (Eureka)

## Ejemplo de Payload

### Crear Usuario
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "1234567890",
  "credential": {
    "username": "johndoe",
    "password": "password123"
  }
}
```

### Crear Dirección
```json
{
  "street": "123 Main St",
  "city": "New York",
  "state": "NY",
  "zipCode": "10001",
  "country": "USA"
}
```

### Crear Credencial
```json
{
  "username": "johndoe",
  "password": "password123"
}
```
