# SaaS Asset Manager — Backend

API REST para la gestión, control y monitoreo de licencias de software en la nube asignadas a los colaboradores de la organización. Consumida por [saasmanager-FrontEnd](../saasmanager-FrontEnd).

## Descripción general

El sistema administra el inventario de proveedores en la nube (AWS, Google Cloud, Azure, etc.), los planes o licencias adquiridas y las asignaciones directas a cada empleado, con autenticación JWT y autorización por roles (`ROLE_ADMIN` / `ROLE_USER`).

## Stack

- Java 17
- Spring Boot 4.1.0 (`spring-boot-starter-parent`)
- Spring Web MVC, Spring Data JPA, Spring Security (`@EnableMethodSecurity`)
- MySQL (`mysql-connector-j`)
- JJWT 0.12.6 (JSON Web Tokens)
- Maven (wrapper `mvnw` / `mvnw.cmd` incluido, no requiere Maven instalado globalmente)

## Requisitos previos

- JDK 17
- Servidor MySQL en ejecución (por defecto puerto `3306`)
- No requiere Maven instalado: usar el wrapper incluido (`./mvnw` o `.\mvnw.cmd`)

## Instalación

```bash
git clone <url-del-repositorio>
cd saasmanager-BackEnd
```

### 1. Preparar la base de datos

```sql
CREATE DATABASE saas_manager CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

El esquema de tablas se crea/actualiza automáticamente al arrancar (`spring.jpa.hibernate.ddl-auto=update`) a partir de las entidades JPA — no hace falta correr DDL a mano. Si querés datos de prueba, el archivo `datos_dummy.sql` en la raíz del repo tiene inserts opcionales para `proveedor_nube`, `licencia_software` y `asignacion_empleado` (no incluye usuarios; esos se crean vía `POST /api/v1/auth/register`).

### 2. Configurar variables de entorno

La configuración vive en `src/main/resources/application.properties`. Los valores por defecto (para desarrollo local) son:

| Propiedad | Variable de entorno equivalente | Default |
|---|---|---|
| `spring.datasource.url` | `SPRING_DATASOURCE_URL` | `jdbc:mysql://localhost:3306/saas_manager?useSSL=false&serverTimezone=UTC` |
| `spring.datasource.username` | `SPRING_DATASOURCE_USERNAME` | `root` |
| `spring.datasource.password` | `SPRING_DATASOURCE_PASSWORD` | `root` |
| `server.port` | `SERVER_PORT` | `8081` |
| `jwt.secret` | `JWT_SECRET` | clave hardcodeada de desarrollo en `JwtUtils.java` (cambiar en producción) |
| `jwt.expiration` | `JWT_EXPIRATION` | `86400000` (ms = 24h) |

`jwt.secret` y `jwt.expiration` no están declaradas en `application.properties`; usan el default de `@Value("${jwt.secret:...}")` en `JwtUtils.java` si no se sobreescriben. Para producción, definilas explícitamente (por variable de entorno o en `application.properties`) — **no** uses el secreto de desarrollo incluido en el código.

Spring Boot mapea variables de entorno a propiedades automáticamente (relaxed binding), así que alcanza con exportarlas antes de levantar el proceso:

```bash
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/saas_manager?useSSL=false&serverTimezone=UTC"
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=tu_password
export JWT_SECRET=una_clave_larga_y_secreta
export JWT_EXPIRATION=86400000
```

En Windows PowerShell: `$env:SPRING_DATASOURCE_PASSWORD = "tu_password"`.

El origen CORS permitido (`http://localhost:4200`) está hardcodeado en `SecurityConfig.java` — no es configurable por variable de entorno; si el frontend corre en otro origen, hay que editarlo ahí.

## Comandos de ejecución

Desde la raíz del proyecto:

**Windows:**
```bash
.\mvnw.cmd spring-boot:run
```

**Linux / macOS:**
```bash
./mvnw spring-boot:run
```

La API queda disponible en `http://localhost:8081` cuando la consola muestra `Started SaasmanagerApplication`.

### Compilar sin ejecutar

```bash
./mvnw clean compile
```

### Generar el JAR

```bash
./mvnw clean package
java -jar target/saasmanager-0.0.1-SNAPSHOT.jar
```

### Tests

```bash
./mvnw test
```

## Arquitectura

```
com.turing.saasmanager
 ├── controller/   # Endpoints REST
 ├── service/      # Reglas de negocio (@Transactional)
 ├── repository/   # Spring Data JPA
 ├── entity/       # Mapeo ORM
 ├── dto/          # Request/response DTOs
 ├── security/     # JWT (filtro, utils) y UserDetailsService
 └── exception/    # Excepciones de dominio + GlobalExceptionHandler
```

## Autenticación y autorización

- `POST /api/v1/auth/login` y `POST /api/v1/auth/register` son públicos; todo lo demás requiere `Authorization: Bearer <token>`.
- El JWT se firma con el email del usuario como subject (`CustomUserDetailsService` busca siempre por email).
- Roles: `ROLE_ADMIN`, `ROLE_USER`. Se asignan vía `@PreAuthorize("hasRole('ADMIN')")` a nivel de clase o método.
- `/api/v1/proveedores/**` y `/api/v1/licencias/**`: solo `ROLE_ADMIN`.
- `/api/v1/asignaciones` (GET lista): `ROLE_ADMIN` ve todas; `ROLE_USER` solo las propias (filtradas por su email). Crear/editar/eliminar/ver por ID: solo `ROLE_ADMIN`.
- `/api/v1/users/me`: cualquier autenticado, solo su propio perfil. `/api/v1/users/{id}`: solo `ROLE_ADMIN`.

## Referencia de endpoints

Todas las respuestas son JSON. Errores siguen el formato `ErrorResponse`: `{ timestamp, status, error, mensaje, path, detalles? }`.

### Auth (`/api/v1/auth`) — público

| Método | Ruta | Body | Descripción |
|---|---|---|---|
| POST | `/login` | `{ email, password }` | Devuelve `{ token, type, username, email, rol }` |
| POST | `/register` | `{ username, email, password, rol? }` | Crea usuario (`rol` default `ROLE_USER`). `201 Created` |

### Usuarios (`/api/v1/users`) — autenticado

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| GET | `/me` | Propio | Perfil del usuario autenticado |
| PUT | `/me` | Propio | Edita `username`/`email`/`password` (requiere `passwordActual` para cambiar `passwordNuevo`) |
| PUT | `/{id}` | ADMIN | Edita cualquier usuario, incluido `rol` |

### Proveedores (`/api/v1/proveedores`) — solo ADMIN

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/` | Listar (`200`) |
| GET | `/{id}` | Por ID (`200` / `404`) |
| GET | `/{id}/dependencias` | `{ licencias, asignaciones }` asociadas |
| POST | `/` | Crear (`201`) |
| PUT | `/{id}` | Actualizar (`200` / `404`) |
| DELETE | `/{id}?cascada=false` | Eliminar. Si tiene licencias asociadas y `cascada=false` → `409`. Con `cascada=true` borra en cascada licencias y sus asignaciones. |

### Licencias (`/api/v1/licencias`) — solo ADMIN

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/` | Listar (`200`) |
| GET | `/{id}` | Por ID (`200` / `404`) |
| POST | `/` | Crear (`201`) |
| PUT | `/{id}` | Actualizar (`200` / `404`) |
| DELETE | `/{id}` | Eliminar (`204` / `404`) |

### Asignaciones (`/api/v1/asignaciones`)

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| GET | `/` | Autenticado | ADMIN: todas. USER: solo las propias (por email) |
| GET | `/{id}` | ADMIN | Por ID |
| POST | `/` | ADMIN | Asignar licencia a empleado |
| PUT | `/{id}` | ADMIN | Modificar asignación |
| DELETE | `/{id}` | ADMIN | Eliminar |

## Ejemplos de peticiones HTTP

**Registrar usuario:**
```bash
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "juan.perez", "email": "juan.perez@empresa.com", "password": "claveSegura123"}'
```

**Login:**
```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "juan.perez@empresa.com", "password": "claveSegura123"}'
```

**Crear un proveedor (requiere token de ADMIN):**
```bash
curl -X POST http://localhost:8081/api/v1/proveedores \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"nombrePlataforma": "Datadog", "categoriaServicio": "Monitoreo"}'
```

**Asignar una licencia a un empleado:**
```bash
curl -X POST http://localhost:8081/api/v1/asignaciones \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"licencia": {"idLicencia": 1}, "correoEmpleado": "juan.perez@empresa.com", "fechaAsignacion": "2026-07-15", "estatusActivo": true}'
```
