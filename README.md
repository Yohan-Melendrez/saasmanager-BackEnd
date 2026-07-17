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

---

## Deploy local — guía paso a paso


### Requisitos previos

| Herramienta | Versión mínima | Verificar con |
|---|---|---|
| JDK | 17 | `java -version` |
| MySQL | 8.x | `mysql --version` |
| Maven | No requerido | Se usa el wrapper del proyecto |

---

### Paso 1 — Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd saasmanager-BackEnd
```

---

### Paso 2 — Preparar la base de datos

Conéctate a MySQL y crea la base de datos si no existe:

```sql
CREATE DATABASE IF NOT EXISTS saas_manager
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

> **No es necesario crear tablas ni correr DDL a mano.**
> Hibernate las crea y actualiza automáticamente al arrancar la aplicación gracias a `spring.jpa.hibernate.ddl-auto=update`.

---

### Paso 3 — Migración de la columna `email` (solo si ya tenías datos)

Si es la **primera vez** que levantás el proyecto, puedes saltarte este paso — Hibernate creará la columna `email` sola.


Si ya tenías la tabla `usuario` con registros previos (**sin** la columna `email`), deberás agregarla manualmente antes de arrancar para evitar el error `NOT NULL constraint failed`:

```sql
USE saas_manager;

-- 1. Agregar la columna permitiendo NULL temporalmente
ALTER TABLE usuario
  ADD COLUMN email VARCHAR(150) NULL UNIQUE AFTER username;

-- 2. Asignar un email provisional a los registros existentes
--    (reemplaza los valores según corresponda)
UPDATE usuario SET email = 'admin@saasmanager.com'     WHERE username = 'admin';
UPDATE usuario SET email = 'usuario@saasmanager.com'   WHERE username != 'admin';

-- 3. Ahora que todos tienen email, vuélvelo obligatorio
ALTER TABLE usuario
  MODIFY COLUMN email VARCHAR(150) NOT NULL;
```

---

### Paso 4 — Configurar la conexión a la base de datos

Edita `src/main/resources/application.properties` con tus credenciales de MySQL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/saas_manager?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD_AQUI
```

Alternativamente puedes usar variables de entorno sin tocar el archivo (Spring Boot las mapea automáticamente):

**Linux / macOS:**
```bash
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/saas_manager?useSSL=false&serverTimezone=UTC"
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=tu_password
export JWT_SECRET=una_clave_larga_y_secreta_para_produccion
```

**Windows PowerShell:**
```powershell
$env:SPRING_DATASOURCE_URL     = "jdbc:mysql://localhost:3306/saas_manager?useSSL=false&serverTimezone=UTC"
$env:SPRING_DATASOURCE_USERNAME = "root"
$env:SPRING_DATASOURCE_PASSWORD = "tu_password"
$env:JWT_SECRET                 = "una_clave_larga_y_secreta_para_produccion"
```

Todas las propiedades disponibles:

| Propiedad | Variable de entorno | Default |
|---|---|---|
| `spring.datasource.url` | `SPRING_DATASOURCE_URL` | `jdbc:mysql://localhost:3306/saas_manager?...` |
| `spring.datasource.username` | `SPRING_DATASOURCE_USERNAME` | `root` |
| `spring.datasource.password` | `SPRING_DATASOURCE_PASSWORD` | `root` |
| `server.port` | `SERVER_PORT` | `8081` |
| `jwt.secret` | `JWT_SECRET` | clave de desarrollo en `JwtUtils.java` |
| `jwt.expiration` | `JWT_EXPIRATION` | `86400000` (ms = 24 h) |

> ⚠️ **Producción:** nunca uses el secreto JWT de desarrollo hardcodeado en el código. Siempre sobrescríbelo con una variable de entorno.

---

### Paso 5 — Levantar la aplicación

**Windows:**
```powershell
.\mvnw.cmd spring-boot:run
```

**Linux / macOS:**
```bash
./mvnw spring-boot:run
```

La aplicación está lista cuando la consola muestra:

```
Started SaasmanagerApplication in X.XXX seconds
```

La API queda disponible en: **`http://localhost:8081`**

---

### Paso 6 — Verificar que todo funciona

Al arrancar por primera vez, el `DataInitializer` carga automáticamente dos usuarios de prueba si la tabla `usuario` está vacía:

| Username | Email | Contraseña | Rol |
|---|---|---|---|
| `admin` | `admin@saasmanager.com` | `admin123` | `ROLE_ADMIN` |
| `carlos.martinez` | `carlos.martinez@empresa.com` | `user123` | `ROLE_USER` |

Prueba el login con curl o cualquier cliente REST (Postman, Insomnia, etc.):

```bash
# Login como admin
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@saasmanager.com", "password": "admin123"}'
```

Respuesta esperada:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "username": "admin",
  "email": "admin@saasmanager.com",
  "rol": "ROLE_ADMIN"
}
```

Guarda el `token` para usarlo en las peticiones protegidas:

```bash
# Ver tu perfil
curl http://localhost:8081/api/v1/users/me \
  -H "Authorization: Bearer <token>"

# Listar proveedores (solo ADMIN)
curl http://localhost:8081/api/v1/proveedores \
  -H "Authorization: Bearer <token>"
```

---

### Otros comandos útiles

```bash
# Solo compilar (sin ejecutar)
./mvnw clean compile

# Generar el JAR ejecutable
./mvnw clean package
java -jar target/saasmanager-0.0.1-SNAPSHOT.jar

# Correr los tests
./mvnw test
```

---

## Arquitectura

```
com.turing.saasmanager
 ├── controller/   # Endpoints REST
 ├── service/      # Reglas de negocio (@Transactional)
 ├── repository/   # Spring Data JPA
 ├── entity/       # Mapeo ORM
 ├── dto/          # Request/response DTOs
 ├── security/     # JWT (filtro, utils) y UserDetailsService
 ├── config/       # DataInitializer (datos de prueba)
 └── exception/    # Excepciones de dominio + GlobalExceptionHandler
```

---

## Autenticación y autorización

- `POST /api/v1/auth/login` y `POST /api/v1/auth/register` son públicos; todo lo demás requiere `Authorization: Bearer <token>`.
- El login se realiza **exclusivamente con el correo electrónico** (`email` + `password`).
- El JWT usa el `username` como subject; `CustomUserDetailsService` busca al usuario por `email` al autenticar.
- Roles: `ROLE_ADMIN`, `ROLE_USER`. Controlados con `@PreAuthorize("hasRole('ADMIN')")`.
- `/api/v1/proveedores/**` y `/api/v1/licencias/**`: solo `ROLE_ADMIN`.
- `/api/v1/asignaciones` (GET lista): `ROLE_ADMIN` ve todas; `ROLE_USER` solo las propias (filtradas por su email).
- `/api/v1/users/me`: cualquier autenticado, solo su propio perfil. `/api/v1/users/{id}`: solo `ROLE_ADMIN`.

---

## Referencia de endpoints

Todas las respuestas son JSON. Los errores siguen el formato `ErrorResponse`:
```json
{ "timestamp": "...", "status": 400, "error": "Bad Request", "mensaje": "...", "path": "..." }
```

### Auth (`/api/v1/auth`) — público

| Método | Ruta | Body | Descripción |
|---|---|---|---|
| POST | `/login` | `{ email, password }` | Devuelve `{ token, type, username, email, rol }` |
| POST | `/register` | `{ username, email, password, rol? }` | Crea usuario. `rol` default `ROLE_USER`. Retorna `201 Created` |

### Usuarios (`/api/v1/users`) — autenticado

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| GET | `/me` | Propio | Perfil del usuario autenticado |
| PUT | `/me` | Propio | Edita `username` / `email` / `passwordNuevo` (requiere `passwordActual` para cambiar contraseña) |
| PUT | `/{id}` | ADMIN | Edita cualquier usuario, incluido `rol` |

**Body para `PUT /me`** — todos los campos son opcionales, solo se actualizan los enviados:
```json
{
  "username":       "nuevo.nombre",
  "email":          "nuevo@empresa.com",
  "passwordActual": "claveAnterior",
  "passwordNuevo":  "claveNueva123"
}
```

### Proveedores (`/api/v1/proveedores`) — solo ADMIN

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/` | Listar (`200`) |
| GET | `/{id}` | Por ID (`200` / `404`) |
| GET | `/{id}/dependencias` | `{ licencias, asignaciones }` asociadas |
| POST | `/` | Crear (`201`) |
| PUT | `/{id}` | Actualizar (`200` / `404`) |
| DELETE | `/{id}?cascada=false` | Eliminar. Si tiene licencias y `cascada=false` → `409`. Con `cascada=true` borra en cascada. |

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

---

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

**Editar perfil propio:**
```bash
curl -X PUT http://localhost:8081/api/v1/users/me \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"email": "nuevo.correo@empresa.com", "passwordActual": "claveSegura123", "passwordNuevo": "nuevaClave456"}'
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
