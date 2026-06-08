# Tests unitarios — Backend

Guía de pruebas automatizadas del API Spring Boot. Describe qué se prueba, cómo ejecutar los tests y cómo agregar nuevos casos.

---

## 1. Resumen

| Aspecto | Detalle |
|---------|---------|
| Framework | JUnit 5 (Jupiter) |
| Mocking | Mockito (`@ExtendWith(MockitoExtension.class)`) |
| Aserciones | AssertJ |
| Smoke test | `@SpringBootTest` con perfil `test` + H2 en memoria |
| Comando | `mvn test` |

Los tests **no requieren SQL Server** ni Docker. El perfil `test` usa H2 en memoria solo para verificar que el contexto Spring arranca.

---

## 2. Ejecutar los tests

### Todos los tests

```bash
cd backend
mvn test
```

### Un test específico

```bash
mvn test -Dtest=CarServiceTest
mvn test -Dtest=CarServiceTest#create_shouldNormalizePlateAndPersistCar
```

### Sin tests (solo compilar)

```bash
mvn package -DskipTests
```

### Desde Docker (solo build, tests en compile stage)

El `Dockerfile` usa `-DskipTests` en la imagen de producción. Ejecuta tests localmente antes de desplegar.

---

## 3. Estructura de tests

```
backend/src/test/
├── java/com/registroautos/
│   ├── RegistroAutosApplicationTests.java   # Smoke: contexto Spring
│   ├── service/
│   │   ├── AuthServiceTest.java
│   │   └── CarServiceTest.java
│   ├── security/
│   │   └── JwtServiceTest.java
│   ├── validation/
│   │   ├── ValidPlateValidatorTest.java
│   │   └── ValidYearValidatorTest.java
│   ├── mapper/
│   │   └── EntityMapperTest.java
│   └── exception/
│       └── GlobalExceptionHandlerTest.java
└── resources/
    └── application-test.yml                 # H2 + JWT de prueba
```

---

## 4. Tipos de tests

### 4.1 Tests unitarios puros (sin Spring)

Aislan una clase con mocks. No levantan contexto de aplicación.

| Clase bajo prueba | Archivo de test | Qué valida |
|-------------------|-----------------|------------|
| `AuthService` | `AuthServiceTest` | Registro, login, email duplicado, normalización |
| `CarService` | `CarServiceTest` | CRUD, ownership, placa duplicada, normalización de placa |
| `JwtService` | `JwtServiceTest` | Generación, extracción de username, validez, secreto corto |
| `ValidPlateValidator` | `ValidPlateValidatorTest` | Formato MWK737 |
| `ValidYearValidator` | `ValidYearValidatorTest` | Rango 1900 – año actual |
| `EntityMapper` | `EntityMapperTest` | Mapeo Entity → DTO |
| `GlobalExceptionHandler` | `GlobalExceptionHandlerTest` | Códigos HTTP y cuerpo JSON de errores |

### 4.2 Smoke test de integración

| Archivo | Anotaciones | Qué valida |
|---------|-------------|------------|
| `RegistroAutosApplicationTests` | `@SpringBootTest`, `@ActiveProfiles("test")` | El contexto Spring Boot arranca con H2 |

---

## 5. Detalle por módulo

### AuthServiceTest

| Test | Escenario | Resultado esperado |
|------|-----------|-------------------|
| `register_shouldPersistUserAndReturnResponse` | Email nuevo | Usuario guardado con email lowercase y password encoded |
| `register_shouldThrowWhenEmailAlreadyExists` | Email duplicado | `DuplicateResourceException` (409) |
| `login_shouldAuthenticateAndReturnToken` | Credenciales válidas | Token JWT + datos de usuario |

**Dependencias mockeadas:** `UserRepository`, `PasswordEncoder`, `JwtService`, `AuthenticationManager`, `CustomUserDetailsService`

### CarServiceTest

| Test | Escenario | Resultado esperado |
|------|-----------|-------------------|
| `findAllForUser_shouldReturnMappedCars` | Listado con filtros | Lista de `CarResponse` |
| `findByIdForUser_shouldReturnCarWhenOwned` | Auto del usuario | `CarResponse` |
| `findByIdForUser_shouldThrowWhenNotFound` | Auto ajeno o inexistente | `ResourceNotFoundException` (404) |
| `create_shouldNormalizePlateAndPersistCar` | Placa `mwk737` | Guardada como `MWK737` |
| `create_shouldThrowWhenPlateAlreadyExists` | Placa duplicada | `DuplicateResourceException` (409) |
| `create_shouldThrowWhenUserNotFound` | userId inválido | `ResourceNotFoundException` (404) |
| `update_shouldUpdateOwnedCar` | Actualización válida | Campos actualizados, `photoUrl` null si vacío |
| `update_shouldThrowWhenDuplicatePlate` | Placa de otro auto | `DuplicateResourceException` (409) |
| `delete_shouldRemoveOwnedCar` | Eliminar propio auto | `carRepository.delete()` invocado |
| `delete_shouldThrowWhenCarNotFound` | ID inexistente | `ResourceNotFoundException` (404) |

### JwtServiceTest

| Test | Escenario | Resultado esperado |
|------|-----------|-------------------|
| `generateToken_shouldReturnNonEmptyToken` | Usuario válido | Token no vacío |
| `extractUsername_shouldReturnSubjectFromToken` | Token generado | Email del subject |
| `isTokenValid_shouldReturnTrueForMatchingUser` | Mismo username | `true` |
| `isTokenValid_shouldReturnFalseForDifferentUser` | Username distinto | `false` |
| `constructor_shouldRejectShortSecret` | Secreto &lt; 32 bytes | `IllegalArgumentException` |

### ValidPlateValidatorTest

| Entrada | Válida |
|---------|--------|
| `MWK737`, `mwk737`, ` ABC123 ` | Sí |
| `ABC-123`, `AB1234`, `123ABC` | No |
| `null`, vacío, espacios | Sí (delega a `@NotBlank`) |

### ValidYearValidatorTest

| Entrada | Válida |
|---------|--------|
| `1900`, año actual | Sí |
| `null` | Sí (delega a `@NotNull`) |
| Año futuro, `1899` | No |

### GlobalExceptionHandlerTest

| Excepción | HTTP | Mensaje |
|-----------|------|---------|
| `ResourceNotFoundException` | 404 | Mensaje de la excepción |
| `DuplicateResourceException` | 409 | Mensaje de la excepción |
| `BadCredentialsException` | 401 | "Credenciales invalidas" |
| `MethodArgumentNotValidException` | 400 | Mapa `fieldErrors` |

---

## 6. Configuración de test

**Archivo:** `src/test/resources/application-test.yml`

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;MODE=MSSQLServer
  jpa:
    hibernate:
      ddl-auto: create-drop

jwt:
  secret: test-jwt-secret-key-with-at-least-32-bytes!!
  expiration-ms: 3600000
```

- **H2** simula SQL Server en modo compatibilidad (`MODE=MSSQLServer`)
- **`create-drop`**: Hibernate crea tablas al iniciar y las elimina al terminar (solo en tests)
- **JWT de prueba**: secreto fijo de 32+ bytes

---

## 7. Convenciones para nuevos tests

### Nomenclatura

```
metodoProbado_escenario_resultadoEsperado
```

Ejemplos:
- `create_shouldThrowWhenPlateAlreadyExists`
- `login_shouldAuthenticateAndReturnToken`

### Tests de servicios

```java
@ExtendWith(MockitoExtension.class)
class MiServiceTest {

    @Mock
    private MiRepository repository;

    @InjectMocks
    private MiService service;

    @Test
    void metodo_escenario_resultado() {
        // Arrange: when(...).thenReturn(...)
        // Act: service.metodo(...)
        // Assert: assertThat(...).isEqualTo(...)
    }
}
```

### Qué mockear

| Capa | Mock | No mockear |
|------|------|------------|
| Service | Repositories, servicios externos | Lógica del propio service |
| Validator | — (clase pura) | — |
| Mapper | — (métodos estáticos) | — |
| Exception handler | `MethodArgumentNotValidException` si aplica | Handler real |

### Qué priorizar al agregar tests

1. Reglas de negocio en servicios (happy path + excepciones)
2. Validadores custom
3. Seguridad JWT
4. Mapeos DTO
5. Handler de excepciones

---

## 8. Cobertura actual y pendientes

### Cubierto

- [x] `AuthService` — registro y login
- [x] `CarService` — CRUD completo
- [x] `JwtService` — token y validación
- [x] Validadores de placa y año
- [x] `EntityMapper`
- [x] `GlobalExceptionHandler`
- [x] Carga de contexto Spring

### Pendiente recomendado

- [ ] Tests de controladores con `@WebMvcTest` + `MockMvc`
- [ ] Tests de seguridad con `@SpringBootTest` + `@AutoConfigureMockMvc`
- [ ] Tests de integración con Testcontainers (SQL Server real)
- [ ] Tests de `CarSpecifications` (filtros dinámicos)
- [ ] JaCoCo para reporte de cobertura en CI

---

## 9. Integración continua (CI)

Ejemplo para GitHub Actions:

```yaml
- name: Run backend tests
  working-directory: backend
  run: mvn test -B
```

Los tests deben pasar sin servicios externos gracias al perfil `test` con H2.

---

## 10. Referencias

- [Informe técnico del backend](backend.md)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/reference/testing/index.html)
