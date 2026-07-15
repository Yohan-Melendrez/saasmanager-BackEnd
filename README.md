# SaaS Asset Manager

Panel interno para la gestión, control y monitoreo de licencias de software en la nube asignadas a los colaboradores de la organización.

## Descripción General

El sistema permite administrar el inventario de proveedores en la nube (AWS, Google Cloud, Azure, etc.), los planes o licencias adquiridas y las asignaciones directas a cada empleado. Está diseñado con una arquitectura por capas en el backend y una base de datos relacional estrictamente normalizada en Tercera Forma Normal (3NF).

---

## Estructura de la Base de Datos (3NF)

El modelo relacional consta de tres tablas conectadas mediante llaves foráneas:

1. **`proveedor_nube`** (`id_proveedor`, `nombre_plataforma`, `categoria_servicio`)
   Catálogo general de plataformas y servicios contratados.
2. **`licencia_software`** (`id_licencia`, `id_proveedor`, `tipo_plan`, `costo_mensual`, `asientos_totales`)
   Detalle de los planes de software asociados a un proveedor en específico.
3. **`asignacion_empleado`** (`id_asignacion`, `id_licencia`, `correo_empleado`, `fecha_asignacion`, `estatus_activo`)
   Control de qué empleado tiene asignada cada licencia y si su acceso se encuentra activo o suspendido.

### ¿El SQL se genera automáticamente?

Sí. Al iniciar la aplicación, Spring Boot gestiona el esquema y los datos mediante dos componentes:
- **Hibernate (`ddl-auto=update`)**: Crea o actualiza automáticamente las tablas en MySQL a partir de las entidades JPA del proyecto.
- **`DataInitializer` (`CommandLineRunner`)**: Si detecta que la base de datos está vacía al arrancar, inserta un set de datos de prueba para poder consultar la API de inmediato.

*Si necesitas ejecutar las consultas SQL manualmente en tu gestor de base de datos, puedes usar el archivo `datos_dummy.sql` incluido en el directorio principal.*

---

## Arquitectura del Sistema

El backend está organizado en paquetes según la responsabilidad de cada componente:

```
com.turing.saasmanager
 ├── controller/   # Endpoints REST y manejo de respuestas HTTP
 ├── service/      # Reglas de negocio y manejo transaccional (@Transactional)
 ├── repository/   # Acceso a datos con Spring Data JPA
 ├── entity/       # Mapeo ORM de las tablas en la base de datos
 └── config/       # Componentes de inicialización y configuración
```

- Inyección de dependencias mediante constructores en los controladores y servicios.
- Manejo de consultas y actualizaciones con programación funcional (`Optional`) para retornar los códigos HTTP correctos sin depender de comprobaciones nulas manuales.

---

## Instrucciones para Ejecutar el Proyecto

### Requisitos previos
- Java JDK 21
- Apache Maven (o el wrapper `./mvnw` incluido)
- Servidor MySQL en ejecución en el puerto `3306`

### 1. Preparar la base de datos
Crea la base de datos en tu servidor MySQL:
```sql
CREATE DATABASE saas_manager CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Revisa las credenciales de conexión en `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/saas_manager?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

### 2. Compilar y ejecutar
Desde la consola, en la carpeta raíz del proyecto:

En Windows:
```bash
.\mvnw.cmd clean spring-boot:run
```

En Linux o macOS:
```bash
./mvnw clean spring-boot:run
```

Una vez que veas en la consola el mensaje de inicio del servidor (`Started SaasmanagerApplication`), la API estará disponible en `http://localhost:8081`.

---

## Referencia de Endpoints REST

Todas las respuestas se devuelven en formato JSON.

### Proveedores (`/api/v1/proveedores`)
- `GET /api/v1/proveedores` - Listar todos los proveedores (`200 OK`)
- `GET /api/v1/proveedores/{id}` - Obtener proveedor por ID (`200 OK` / `404 Not Found`)
- `POST /api/v1/proveedores` - Registrar nuevo proveedor (`201 Created`)
- `PUT /api/v1/proveedores/{id}` - Actualizar proveedor (`200 OK` / `404 Not Found`)
- `DELETE /api/v1/proveedores/{id}` - Eliminar proveedor (`204 No Content` / `404 Not Found`)

### Licencias (`/api/v1/licencias`)
- `GET /api/v1/licencias` - Listar todas las licencias (`200 OK`)
- `GET /api/v1/licencias/{id}` - Obtener licencia por ID (`200 OK` / `404 Not Found`)
- `POST /api/v1/licencias` - Registrar nueva licencia (`201 Created`)
- `PUT /api/v1/licencias/{id}` - Actualizar plan, costo o asientos (`200 OK` / `404 Not Found`)
- `DELETE /api/v1/licencias/{id}` - Eliminar licencia (`204 No Content` / `404 Not Found`)

### Asignaciones (`/api/v1/asignaciones`)
- `GET /api/v1/asignaciones` - Listar todas las asignaciones (`200 OK`)
- `GET /api/v1/asignaciones/{id}` - Obtener asignación por ID (`200 OK` / `404 Not Found`)
- `POST /api/v1/asignaciones` - Asignar licencia a empleado (`201 Created`)
- `PUT /api/v1/asignaciones/{id}` - Modificar asignación o estatus (`200 OK` / `404 Not Found`)
- `DELETE /api/v1/asignaciones/{id}` - Eliminar asignación (`204 No Content` / `404 Not Found`)

---

## Ejemplos de Peticiones HTTP

**Crear un proveedor:**
```bash
curl -X POST http://localhost:8081/api/v1/proveedores \
  -H "Content-Type: application/json" \
  -d '{"nombrePlataforma": "Datadog", "categoriaServicio": "Monitoreo"}'
```

**Asignar una licencia a un empleado:**
```bash
curl -X POST http://localhost:8081/api/v1/asignaciones \
  -H "Content-Type: application/json" \
  -d '{"licencia": {"idLicencia": 1}, "correoEmpleado": "juan.perez@empresa.com", "fechaAsignacion": "2026-07-15", "estatusActivo": true}'
```
