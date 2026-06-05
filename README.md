# Registro y Gestion de Autos

Sistema fullstack para registro de usuarios, autenticacion JWT y CRUD de vehiculos personales.

## Stack

- **Backend:** Java 17, Spring Boot 3, Spring Security (JWT), Spring Data JPA, Springdoc OpenAPI
- **Frontend:** React 19, Vite, TypeScript, Axios, Zustand, Tailwind CSS
- **Base de datos:** SQL Server 2022
- **DevOps:** Docker y Docker Compose

## Requisitos

- Docker Desktop (o Docker Engine + Compose)
- Puertos libres en tu maquina para los valores definidos en `.env` (por defecto: `80`, `8080`, `1433`)

## Ejecucion

1. Clona el repositorio:

```bash
git clone https://github.com/andresjose84/solucion-registro-autos.git
cd solucion-registro-autos
```

2. Crea las variables de entorno:

```bash
cp .env.example .env
```

3. Levanta todos los servicios:

```bash
docker compose up --build
```

4. Accede a la aplicacion (usa los puertos configurados en `.env`):

| Servicio | URL (valores por defecto) |
|----------|---------------------------|
| Frontend | http://localhost:`FRONTEND_PORT` |
| API Swagger | http://localhost:`BACKEND_PORT`/swagger-ui.html |
| SQL Server | `localhost:DB_PORT` |

### Puertos personalizados

Si `80`, `8080` o `1433` estan ocupados en tu PC, edita `.env` antes de levantar Docker:

```env
FRONTEND_PORT=3000
BACKEND_PORT=8081
DB_PORT=1434
```

Luego reinicia los servicios:

```bash
docker compose down
docker compose up --build
```

Los contenedores siguen usando sus puertos internos (`80`, `8080`, `1433`); solo cambia el puerto expuesto en tu maquina. Si cambias `BACKEND_PORT`, actualiza tambien `baseUrl` en Postman (ej. `http://localhost:8081`).

### Credenciales demo

- **Email:** `demo@registroautos.com`
- **Password:** `Demo123!`

El usuario demo incluye 3 autos precargados (placas: MWK737, ABC123, XYZ456).

## Desarrollo local (sin Docker completo)

### Base de datos

Solo la base de datos:

```bash
docker compose up db db-init
```

### Backend

```bash
cd backend
mvn spring-boot:run
```

Variables por defecto en `application.yml` apuntan a SQL Server local en `localhost:1433`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

El proxy de Vite redirige `/api` hacia `http://localhost:8080`.

## API principal

### Autenticacion (`/api/v1/auth`)

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| POST | `/register` | Registrar usuario |
| POST | `/login` | Login y obtener JWT |

### Autos (`/api/v1/cars`) — requiere JWT

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | `/` | Listar autos (`?search=&brand=&year=`) |
| GET | `/{id}` | Detalle |
| POST | `/` | Crear auto |
| PUT | `/{id}` | Actualizar |
| DELETE | `/{id}` | Eliminar |

## Validaciones

- **Placa:** formato `ABC123` (3 letras + 3 digitos)
- **Año:** no puede ser futuro (1900 – año actual)
- **Password:** minimo 8 caracteres, 1 mayuscula, 1 numero

## Documentacion tecnica

Informes detallados de arquitectura, tecnologias y convenciones:

- [Backend — informe tecnico](docs/backend.md)
- [Frontend — informe tecnico](docs/frontend.md)
- [Requisitos funcionales del proyecto](docs/registro-de-autos.md)

## Estructura del proyecto

```
backend/          # API Spring Boot
frontend/         # SPA React
seed/             # Scripts SQL (seed/init/) e inicializacion
docker-compose.yml
```

## Swagger con JWT

1. Abre http://localhost:8080/swagger-ui.html
2. Ejecuta `POST /api/v1/auth/login` con las credenciales demo
3. Copia el token de la respuesta
4. Pulsa **Authorize** e ingresa: `Bearer <token>`

## Coleccion Postman

Importa los archivos de la carpeta `postman/`:

1. **Registro-Autos.postman_collection.json** — todos los endpoints con ejemplos
2. **Registro-Autos-Local.postman_environment.json** — variables para entorno local (opcional)

Pasos:
1. Postman → **Import** → selecciona ambos archivos
2. Activa el entorno **Registro Autos - Local**
3. Ejecuta **Auth > Login (demo)** (guarda el token JWT automaticamente)
4. Prueba los endpoints de **Autos**

## Detener servicios

```bash
docker compose down
```

Para eliminar tambien el volumen de datos:

```bash
docker compose down -v
```
