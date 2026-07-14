# Día 3 — Dockerización

## Qué se agregó

| Archivo | Para qué |
|---|---|
| `eureka-server/Dockerfile`, `api-gateway/Dockerfile`, `ms-auth/Dockerfile`, `ms-ventas/Dockerfile` | Los 4 que faltaban (inventario y rrhh ya los tenían) |
| `docker-compose.yml` (raíz) | Los 6 servicios + MySQL, en una sola red |
| `build-jars.ps1` | Compila los 6 JAR de una pasada |
| `.env.example` | Plantilla de credenciales (el `.env` real no se commitea) |

También cambié los `application.yml` de `eureka-server`, `ms-auth`, `ms-ventas` y `api-gateway`:
la URL de Eureka pasó de estar fija en `localhost` a ser `${EUREKA_URL:http://localhost:8761/eureka/}`.
El fallback conserva el comportamiento de siempre al correr con `.\run-*.ps1`, y dentro de Docker
el compose inyecta `http://eureka-server:8761/eureka/`. **Dentro de Docker no existe `localhost`**:
cada contenedor es una máquina aparte y se llaman entre sí por el nombre del servicio.

---

## Pasos

### 1. Crear el `.env`

Copia `.env.example` como `.env` y rellena con las credenciales reales (las mismas de tus `run-*.ps1`):

```powershell
copy .env.example .env
notepad .env
```

Lo importante:
- `DB_PASSWORD` → la de Neon
- `JWT_SECRET` → **el mismo** que usa `run-ms-auth.ps1`. Si no coincide, el gateway rechaza todos los tokens.

El `.env` ya está en el `.gitignore`.

### 2. Apagar todo lo que está corriendo a mano

Ctrl+C en las 6 terminales, y baja el MySQL viejo (el compose de la raíz levanta el suyo):

```powershell
cd ms-rrhh
docker compose -f docker-compose-mysql.yml down
cd ..
```

Los puertos 8080-8084, 8761 y 3306 tienen que quedar libres.

### 3. Compilar los JAR

```powershell
.\build-jars.ps1
```

Los `Dockerfile` copian el `.jar` ya compilado, así que este paso es obligatorio.
**Cada vez que cambies código, hay que volver a correrlo.**

### 4. Levantar todo

```powershell
docker compose up --build
```

La primera vez tarda (descarga la imagen de Java y arma 6 imágenes). Para verlo en segundo plano:

```powershell
docker compose up --build -d
docker compose logs -f
```

### 5. Verificar

El orden de arranque lo maneja el compose solo: los `healthcheck` + `depends_on` hacen que
los microservicios esperen a que Eureka esté sano, y `ms-rrhh` espera además a MySQL.

```powershell
docker compose ps          # los 7 contenedores arriba
.\verificar-servicios.ps1 -Usuario admin -Password 123456
```

El script funciona igual que antes, porque los puertos están publicados al host.
**Todo debe salir en verde, exactamente como con los `run-*.ps1`.** Esa es la prueba de que
la dockerización no rompió nada.

---

## Comandos útiles

```powershell
docker compose ps                      # estado de los contenedores
docker compose logs -f ms-inventario   # logs de un servicio
docker compose restart ms-ventas       # reiniciar uno solo
docker compose down                    # apagar todo (conserva los datos de MySQL)
docker compose down -v                 # apagar Y borrar el volumen de MySQL
docker compose up --build ms-rrhh      # reconstruir un solo servicio
```

## Si algo falla

- **Un microservicio no arranca** → `docker compose logs ms-xxx`. Casi siempre es una variable
  de entorno faltante en el `.env`.
- **Login devuelve 401 dentro de Docker pero funcionaba antes** → el `JWT_SECRET` del `.env` no
  es el mismo que el de `run-ms-auth.ps1`.
- **`ms-rrhh` no conecta a MySQL** → `docker compose logs mysql-rrhh`. Si el volumen ya existía de
  antes, el `init.sql` no se re-ejecuta: `docker compose down -v` y vuelve a levantar.
- **503 en el gateway** → Eureka aún no propagó las instancias. Espera 30-60 s.
- **Puerto ocupado** → quedó algo del arranque manual. Cierra esas terminales.

---

## Evidencia para la entrega

Captura estas tres:

1. `docker compose ps` con los 7 contenedores en `Up`
2. `.\verificar-servicios.ps1` todo en verde **corriendo sobre Docker**
3. Docker Desktop → pestaña Containers, mostrando el stack completo

---

## Lo que queda (día 4)

- Cuenta de Google Cloud + `gcloud` CLI
- Subir las 6 imágenes a Artifact Registry
- `kompose convert` para generar los YAML base
- `StatefulSet` + `PVC` para MySQL
- `Secret` con `JWT_SECRET` y credenciales de Neon
- `Service` tipo `LoadBalancer` para el gateway
