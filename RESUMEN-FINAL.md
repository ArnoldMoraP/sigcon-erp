# Caso Siderúrgica Perú — Resumen final (estado del proyecto)

Este documento es el **resumen único y definitivo**: qué se corrigió, qué queda
pendiente, y el **orden exacto** para levantar el proyecto. Reemplaza cualquier
instrucción anterior.

---

## 1. Errores corregidos

| # | Problema (del diagnóstico) | Estado | Dónde se arregló |
|---|---|---|---|
| 1 | El gateway no validaba JWT; se le podía pegar a cualquier ruta sin token | ✅ Corregido | Fase 2 — filtro JWT en el gateway |
| 2 | Puertos 8081-8084 expuestos al host (se saltaba el gateway) | ✅ Corregido | Fase 2 — solo 8080 y 8761 públicos |
| 3 | Healthcheck de Eureka roto (faltaba `actuator`) → todo colgado | ✅ Corregido | Fase 0 — actuator + `/actuator/health` |
| 4 | Bases Neon posiblemente vacías | ⚠️ Operativo (lo corres tú) | Fase 1 — script de verificación + pasos |
| 5 | Bug de indentación YAML en ms-auth (`logging` dentro de `jwt`) | ✅ Corregido | Fase 3 |
| 6 | `JwtUtil` codificaba el secreto en vez de usar sus bytes | ✅ Corregido | Fase 3 (y ahora coincide con el gateway) |
| 7 | Escritura distribuida no atómica (stock vs pedido) | ✅ Mitigado | Fase 4 — compensación (Saga) |
| 8 | No existía Config Server | ✅ Corregido | Extra — `config-server` + `config-repo/` |
| 9 | `show-sql` inconsistente entre servicios | ✅ Corregido | Fase 3 — todo en `false` |
| 10 | Build de Docker no autocontenido (dependía de `build-jars.ps1`) | ✅ Corregido | Fase 5 — Dockerfile multi-stage |
| 11 | No todos los datos en contenedores | ✅ Aclarado (por diseño) | Documentado (Neon gestionado a propósito) |
| — | 16 archivos venían corruptos (0 bytes) en el `.rar` | ✅ Recuperados | Desde el historial de Git del proyecto |

### Detalle de lo que quedó funcionando
- **Seguridad real:** el gateway valida el `access token` antes de enrutar. Sin token
  → 401. Rutas públicas: `/auth/login`, `/auth/refresh`, `/actuator/health` y los
  `OPTIONS` (preflight CORS). Reenvía la identidad a los servicios en `X-Auth-User` /
  `X-Auth-Rol`.
- **Arranque en orden:** `config-server → eureka-server → (mysql) → 4 microservicios +
  gateway`, todo por healthchecks.
- **Un solo comando:** `docker compose up --build` compila y levanta todo (ya no hace
  falta `build-jars.ps1`).
- **Config centralizada:** toda la configuración vive en la carpeta `config-repo/`.
- **Compensación de stock:** si el pedido no se guarda tras descontar stock, se repone.

---

## 2. Qué queda pendiente / riesgos honestos

Esto es lo que **todavía tienes que hacer o tener en cuenta** — no está resuelto en el
código:

1. **Poblar y verificar Neon (Fase 1).** Es lo único imprescindible que depende de ti:
   correr la carga de datos contra tu Neon y verificar que las 3 bases tengan filas.
   Si esto no se hace, los contenedores arrancan igual pero "no devuelven nada" y
   parece que no funciona. (Pasos exactos en la sección 3.)

2. **Verificar columnas inferidas de inventario.** Las columnas de `compras`/`almacen`
   se infirieron sin `pg_dump`. Corre `ms-inventario/schema-check.sql` en Neon y
   compara con las `@Column` de las entidades. Si una no calza, ajusta el `@Column`.

3. **No se pudo compilar ni levantar en el entorno donde se hicieron los cambios**
   (sin acceso a Maven Central ni a Neon). Se validó la **sintaxis** de los 14 YAML,
   los 7 `pom.xml` (XML bien formado) y el `docker-compose.yml`, y la estructura del
   código Java. La **prueba real (compilar + levantar) la haces tú** con
   `docker compose up --build`.

4. **Los manifiestos de Kubernetes (`k8s/` y `k8s-oracle/`) NO se actualizaron.**
   Solo se corrigió el despliegue con Docker Compose. Si vas a evaluar/desplegar con
   Kubernetes, esos YAML todavía reflejan el estado viejo (puertos abiertos, sin
   gateway con JWT, sin config-server, imágenes de una sola etapa) y habría que
   sincronizarlos. **Para la entrega con `docker compose` no afecta.**

5. **Modelo de seguridad "de borde".** ms-ventas, ms-inventario y ms-rrhh no tienen
   seguridad propia: confían en que el único acceso es por el gateway (sus puertos
   están cerrados). Es correcto para este alcance, pero si alguien entrara a la red
   interna de Docker, esos servicios no tienen una segunda barrera. Mejora futura:
   validar también el JWT (o las cabeceras `X-Auth-*`) dentro de cada servicio.

6. **Ventana pequeña en la compensación (Saga).** La compensación cubre el caso típico
   (fallo al guardar el pedido). Un fallo justo en el commit final sigue siendo una
   ventana mínima. La solución "de libro" (estado intermedio `APROBANDO`) quedó
   documentada como mejora, no implementada, para no tocar la máquina de estados del
   frontend.

7. **Higiene de secretos.** El `.env` tiene credenciales reales (Neon + JWT). No lo
   subas a repos públicos. El `JWT_SECRET` de ejemplo conviene cambiarlo por uno más
   largo y aleatorio para la entrega.

8. **Detalle menor latente:** en `ms-auth/security/JwtFilter.java`, `shouldNotFilter`
   compara con `/login` en vez de `/auth/login`. Hoy no rompe nada (el `SecurityConfig`
   ya permite `/auth/login`, y el gateway es la puerta principal), pero es una
   inconsistencia que conviene alinear si algún día ms-auth se expone directo.

---

## 3. ORDEN EXACTO para levantar el proyecto

> Sigue estos pasos **en este orden**. El error más común es hacer `docker compose up`
> con Neon vacío.

### Paso 0 — Requisitos (una sola vez)
- Docker Desktop **abierto y corriendo**.
- Archivo **`.env`** creado en la raíz (copia de `.env.example`) con tus credenciales
  reales de Neon y tu `JWT_SECRET`.
- **`psql`** instalado (ya lo tienes en `C:\Program Files\PostgreSQL\18\bin`).

### Paso 1 — Poblar Neon (ANTES de Docker)
En PowerShell, en la carpeta del proyecto. Por cada base va **primero estructura,
luego datos**:
```powershell
$env:PGPASSWORD="TU_PASSWORD_DE_NEON"
$PG="C:\Program Files\PostgreSQL\18\bin\psql.exe"
$H="ep-wispy-bar-ap1s32ne-pooler.c-7.us-east-1.aws.neon.tech"; $U="neondb_owner"

# auth_db
& $PG "postgresql://$U@$H/auth_db?sslmode=require"       -f db-dumps\01-auth-limpio.sql
& $PG "postgresql://$U@$H/auth_db?sslmode=require"       -f db-dumps\01-auth-datos-limpio.sql
# ventas_db
& $PG "postgresql://$U@$H/ventas_db?sslmode=require"     -f db-dumps\02-ventas-limpio.sql
& $PG "postgresql://$U@$H/ventas_db?sslmode=require"     -f db-dumps\02-ventas-datos-limpio.sql
# inventario_db
& $PG "postgresql://$U@$H/inventario_db?sslmode=require" -f db-dumps\03-inventario-limpio.sql
& $PG "postgresql://$U@$H/inventario_db?sslmode=require" -f db-dumps\03-inventario-datos-limpio.sql
```
La base de **ms-rrhh (MySQL) NO se toca aquí**: Docker la crea y la llena sola.

### Paso 2 — Verificar que Neon quedó con datos
En el **SQL Editor de Neon (web)**, seleccionando cada base arriba, pega los bloques de
`db-dumps/verificacion-post-migracion.sql`. Todos los conteos deben dar **> 0**.
Mínimo crítico: en `auth_db`, `SELECT count(*) FROM seguridad.usuario;` ≥ 1.

### Paso 3 — Levantar todo con Docker
```powershell
docker compose up --build
```
La primera vez tarda (baja Maven y compila los 7 JAR dentro de Docker). Arranca solo en
el orden correcto: config-server → eureka → mysql → microservicios → gateway.

### Paso 4 — Confirmar que está todo arriba
```powershell
docker compose ps
```
Todos deben estar `healthy`. En `http://localhost:8761` (Eureka) deben aparecer los 5
servicios registrados.

### Paso 5 — Probar que es funcional (todo por el puerto 8080)
```powershell
# 1) Login -> devuelve accessToken
$r = Invoke-RestMethod -Uri http://localhost:8080/auth/login -Method Post `
     -ContentType "application/json" `
     -Body '{"username":"TU_USUARIO","password":"TU_PASSWORD"}'
$token = $r.accessToken

# 2) Sin token -> debe dar 401 (prueba la seguridad)
Invoke-RestMethod -Uri http://localhost:8080/inventario     # 401 Unauthorized

# 3) Con token -> debe devolver datos
Invoke-RestMethod -Uri http://localhost:8080/inventario -Headers @{ Authorization = "Bearer $token" }
```
Si el (2) da 401 y el (3) devuelve datos: **Fases 1, 2 y 3 funcionando.**
Para la Fase 4: aprueba un pedido (`PATCH /pedidos/{id}/estado` con `APROBADO`) y
verifica que el stock del producto baje en `/inventario`.

### Resumen en una línea
**1) Datos a Neon → 2) Verificar Neon → 3) `docker compose up --build` → 4) Ver que
estén `healthy` → 5) Login y pruebas por el 8080.**

---

## 4. Cómo apagar / reiniciar limpio
```powershell
docker compose down            # apaga y borra contenedores (conserva datos de MySQL)
docker compose down -v         # además borra el volumen de MySQL (rrhh se recarga)
docker compose up --build      # vuelve a levantar
```
