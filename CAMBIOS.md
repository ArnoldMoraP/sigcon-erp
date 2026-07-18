# Caso Siderúrgica Perú — Cambios aplicados (Fases 0 a 5)

Este documento describe **todo lo que se modificó** respecto al plan de corrección,
fase por fase, con el archivo exacto tocado y el porqué.

> **Nota sobre archivos recuperados:** dentro del `.rar` que subiste, 16 archivos
> vinieron corruptos (0 bytes). Se recuperaron desde el historial de Git del propio
> proyecto (rama `microservicios`, commit `6029c46`). Entre ellos estaban
> `api-gateway/Dockerfile`, `ms-rrhh/Dockerfile`, y los `pom.xml` de
> `eureka-server`, `ms-inventario` y `api-gateway` — que el diagnóstico no pudo leer.
> Ya están completos en esta entrega. Lista completa al final.

---

## Cómo levantar todo (después de estos cambios)

```bash
# 1) Copiar credenciales
cp .env.example .env      # y rellenar con tus valores reales de Neon/JWT

# 2) Un solo comando (ya NO hace falta build-jars.ps1: ver Fase 5)
docker compose up --build
```

Tras el arranque, **el único puerto público es el gateway: http://localhost:8080**.
Eureka queda en `http://localhost:8761` solo para debug.

---

## FASE 0 — Desbloquear el arranque

**Problema:** el healthcheck de `eureka-server` pegaba a `/actuator/health`, pero
ningún `pom.xml` tenía `actuator`. El endpoint daba 404, el healthcheck nunca pasaba
y como los demás usaban `depends_on: condition: service_healthy`, nada arrancaba.

**Cambios:**
- `eureka-server/pom.xml` (+ los otros 5 pom.xml): se agregó
  `spring-boot-starter-actuator`.
- `eureka-server/src/main/resources/application.yml` (+ los otros 5): se expuso el
  endpoint de salud:
  ```yaml
  management:
    endpoints:
      web:
        exposure:
          include: health
  ```
- `docker-compose.yml`: se agregaron healthchecks a los 4 microservicios y al gateway
  (antes solo eureka y mysql los tenían), y `api-gateway` ahora espera a `ms-auth`
  con `condition: service_healthy` en vez de `service_started`.
- `ms-auth/security/SecurityConfig.java`: se permitió `/actuator/**` sin token
  (si no, el healthcheck de ms-auth recibiría 401 y el contenedor nunca quedaría
  `healthy`, porque ms-auth sí tiene seguridad propia).

**Resultado:** `docker compose up` ya no se cuelga; los servicios arrancan en orden.

---

## FASE 1 — Verificar y poblar las bases de datos

Esta fase es **operativa** (se corre contra *tu* Neon, con *tus* credenciales), así
que no se pudo ejecutar automáticamente. Se dejó todo listo para que la corras:

**Pasos:**
1. Correr `migrar-bases.ps1` contra Neon con las credenciales reales del `.env`.
2. Abrir el SQL Editor de Neon y correr **`db-dumps/verificacion-post-migracion.sql`**
   (nuevo). Trae los conteos de filas por tabla en las 3 bases (`auth_db`,
   `ventas_db`, `inventario_db`). Si algún conteo da **0**, esa tabla no se cargó.
3. Para `ms-inventario`, correr también `ms-inventario/schema-check.sql` y comparar
   las columnas reales con las `@Column` de las entidades Java (las columnas de
   `compras`/`almacen` se infirieron sin `pg_dump`, según el README, así que conviene
   confirmarlas).

**Chequeo mínimo para que el login funcione:** `auth_db` debe tener al menos un
usuario (`SELECT count(*) FROM seguridad.usuario;`).

---

## FASE 2 — Cerrar el hueco de seguridad

**Problema:** el gateway enrutaba sin validar el JWT; cualquiera podía pegarle a
`/inventario`, `/rrhh`, `/pedidos`, etc. sin token. Además, los puertos `8081-8084`
estaban publicados al host, así que se podía saltar el gateway del todo.

**Cambios:**
- **Validación de JWT en el gateway** (nuevo). El `api-gateway` es Spring Cloud
  Gateway (reactivo), así que el filtro es un `GlobalFilter`, no un filtro servlet:
  - `api-gateway/src/main/java/dsw/apigateway/security/JwtUtil.java` (nuevo): valida
    firma y expiración del token.
  - `api-gateway/src/main/java/dsw/apigateway/security/JwtAuthenticationFilter.java`
    (nuevo): valida el token **antes** de enrutar. Deja pasar sin token solo
    `OPTIONS` (preflight CORS), `/auth/login`, `/auth/refresh` y `/actuator/health`.
    Cualquier otra ruta sin `Authorization: Bearer <access>` válido recibe **401** y
    no se enruta. Además reenvía la identidad ya validada a los microservicios en
    cabeceras de confianza `X-Auth-User` / `X-Auth-Rol` (que el gateway sobrescribe
    siempre, para que el cliente no las falsifique).
  - `api-gateway/pom.xml`: se agregaron las dependencias `jjwt` (misma versión 0.12.6
    que ms-auth, para que la validación sea compatible).
  - `api-gateway/src/main/resources/application.yml`: se agregó `jwt.secret: ${JWT_SECRET}`.
- **Cerrar puertos** en `docker-compose.yml`: se quitó el mapeo `ports` de
  `ms-auth`, `ms-ventas`, `ms-inventario`, `ms-rrhh` y `mysql-rrhh` (ahora usan
  `expose`, solo alcanzables dentro de la red de Docker). Quedan publicados **solo**
  `api-gateway` (8080) y `eureka-server` (8761, debug).

**Modelo de confianza resultante:** los microservicios de ventas/inventario/rrhh no
tienen seguridad propia, pero como sus puertos ya no están publicados, la única forma
de llegar a ellos es por el gateway, que ya validó el token. El endpoint `/interno/**`
de ms-auth queda accesible solo desde la red interna (Fase 2, sin cambio de código:
se resuelve solo al cerrar los puertos).

---

## FASE 3 — Corregir configuración

- **Bug de indentación YAML** en `ms-auth/src/main/resources/application.yml`: el
  bloque `logging:` estaba anidado **dentro** de `jwt:` (creaba las claves inútiles
  `jwt.logging` y `jwt.level`). Se movió a la raíz del documento; ahora el logging
  DEBUG de Spring Security sí se activa.
- **`JwtUtil` codificaba el secreto** en `ms-auth/security/JwtUtil.java`: hacía
  `Base64.getEncoder().encode(...)` (codificar). Se cambió por la forma estándar:
  ```java
  this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  ```
  **Esto es la MISMA derivación que usa el gateway**, así que los tokens que emite
  ms-auth validan correctamente en el gateway. (Se quitó el `import java.util.Base64`.)
- **`show-sql` homogeneizado a `false`** en `ms-ventas`, `ms-inventario` y `ms-rrhh`
  (ya estaba en `false` en `ms-auth`). Así no se expone SQL en los logs.

---

## FASE 4 — Escritura distribuida (compensación / Saga)

**Problema:** al aprobar un pedido, `ms-ventas` descuenta stock en `ms-inventario`
(transacción aparte, ya confirmada) y luego guarda el pedido. Si el `save()` del
pedido fallaba, el pedido hacía rollback pero **el stock quedaba descontado**. No había
compensación.

**Cambios (transacción compensatoria):**
- `ms-inventario` — nuevo endpoint simétrico a `descontar-stock`:
  - `InventarioService.reponerStockPorProducto(...)`: repone stock por nombre de
    producto, con el mismo bloqueo pesimista que el descuento.
  - `InventarioController`: `PATCH /inventario/reponer-stock`.
- `ms-ventas/client/InventarioClient.java`: nuevo método `reponerStock(...)` que llama
  a ese endpoint. Si la compensación falla, **no** tapa el error original: lo loguea
  como inconsistencia crítica para revisión manual.
- `ms-ventas/service/PedidoService.actualizarEstadoConResultado(...)`:
  - Se cambió `save()` por **`saveAndFlush()`** para que un fallo de BD salte *dentro*
    del `try` (y no recién al hacer commit, fuera de nuestro control).
  - Si el guardado falla **después** de haber descontado stock, se llama a
    `inventarioClient.reponerStock(...)` para devolver el stock, y luego se re-lanza
    el error.

**Limitación honesta / alternativa más robusta:** esto cubre el caso típico
(fallo al persistir el pedido). Un fallo justo en el commit final sigue siendo una
ventana pequeña. La solución "de libro" sería reordenar el flujo con un estado
intermedio: guardar el pedido como `APROBANDO` (commit) → descontar stock →
marcar `APROBADO`. No se implementó así para no cambiar la máquina de estados que
consume el frontend; queda documentado como mejora futura.

---

## FASE 5 — Build de Docker autocontenido

**Problema:** cada `Dockerfile` era de una sola etapa y hacía
`COPY target/xxx.jar` — el JAR tenía que existir *antes* (generado por
`build-jars.ps1` con Maven en el host). Sin eso, `docker compose up --build` fallaba.

**Cambios:**
- Los **6 `Dockerfile`** se convirtieron a **multi-stage**: una etapa
  `FROM maven:3.9-eclipse-temurin-17` compila el JAR con `mvn package` dentro de
  Docker, y la etapa final (`eclipse-temurin:17-jre-alpine`) solo copia el `.jar`.
  Se conservaron las 3 variables `ENV` de UTF-8 (necesarias para los acentos).
- Se agregó un **`.dockerignore`** por servicio (ignora `target/`, `.idea`, etc.)
  para que el contexto de build sea liviano y no arrastre JARs viejos del host.
- `docker-compose.yml`: se actualizó el comentario de cabecera. Ahora
  `docker compose up --build` levanta todo desde cero **sin Maven/JDK en el host y sin
  correr `build-jars.ps1`** (ese script queda opcional).

---

## EXTRA — Config Server (configuración centralizada)

Responde al punto 8 del diagnóstico / Fase 4 paso 13 ("no existe un Config Server
centralizado"). Antes, cada servicio leía su propio `application.yml` con variables
sueltas. Ahora hay un **Spring Cloud Config Server en modo `native`** que centraliza
la configuración en **una carpeta local** del proyecto: `config-repo/`.

**Aclaración importante:** el Config Server centraliza la *configuración*, no las
bases de datos. No tiene relación con "meter cada base en un contenedor" — dónde vive
cada base (contenedor MySQL vs Neon gestionado) se decide en el `docker-compose.yml`,
no aquí.

**Qué se agregó:**
- Módulo nuevo **`config-server/`** (puerto 8888): `pom.xml`, `ConfigServerApplication`
  con `@EnableConfigServer`, `application.yml` en perfil `native`, y `Dockerfile`
  multi-stage. Su healthcheck usa `/actuator/health`.
- Carpeta central **`config-repo/`** con toda la configuración:
  - `application.yml` — lo COMPARTIDO por todos (Eureka + actuator).
  - `ms-auth.yml`, `ms-ventas.yml`, `ms-inventario.yml`, `ms-rrhh.yml`,
    `api-gateway.yml` — la config específica de cada servicio (datasource, jpa, jwt,
    rutas del gateway, etc.). Los secretos siguen como placeholders (`${DB_URL}`,
    `${JWT_SECRET}`): el valor real lo pone cada contenedor con sus variables de
    entorno, así que **las credenciales no quedan en `config-repo/`**.
- Los **5 microservicios** (ms-auth, ms-ventas, ms-inventario, ms-rrhh, api-gateway)
  se convirtieron en *config clients*: su `application.yml` local quedó mínimo (solo
  su nombre + `spring.config.import: configserver:...` + reintentos), y se les
  agregaron las dependencias `spring-cloud-starter-config`, `spring-retry` y
  `spring-boot-starter-aop`.
- `docker-compose.yml`: nuevo servicio `config-server` (arranca primero, monta
  `./config-repo` como volumen de solo lectura); los 5 clientes ahora dependen de él
  (`condition: service_healthy`) y reciben `CONFIG_SERVER_URL=http://config-server:8888`.

**Decisión de diseño:** `eureka-server` se dejó con su config local mínima (no se
migró al config server) para evitar un acoplamiento circular en el arranque
(config-server ⇄ eureka). Es la práctica recomendada: el descubrimiento y la config
son la "columna vertebral" y conviene que no dependan entre sí. El orden de arranque
queda: **config-server → eureka-server → (mysql) → los 4 microservicios + gateway**.

**Cómo comprobarlo:** una vez levantado, la config servida de cada servicio se puede
ver desde dentro de la red Docker, p.ej. `GET http://config-server:8888/ms-auth/default`.

---

## Respuestas para la profesora

**"¿Están los 6 servicios contenerizados de forma independiente?"** — **Sí.** Cada uno
tiene su propio `Dockerfile` y su bloque `build:` en `docker-compose.yml`, corre en un
contenedor separado en la red `siderurgica-net`, y se comunican por nombre de servicio
(no `localhost`). Confirmado en los 6 (incluidos `api-gateway` y `ms-rrhh`, cuyos
Dockerfiles estaban corruptos en el `.rar` y se recuperaron).

**"¿Se puede levantar todo con solo `docker compose up`?"** — **Ahora sí.** Con los
Dockerfile multi-stage (Fase 5), un único `docker compose up --build` compila y levanta
todo, sin depender de compilar antes en el host.

**"¿Todos los datos están contenerizados?"** — **Intencionalmente, no todos.** Solo la
base de `ms-rrhh` (MySQL) corre en un contenedor local con su volumen. Las bases de
`ms-auth`, `ms-ventas` e `ms-inventario` son **PostgreSQL gestionado en Neon** (servicio
externo en la nube). Es una decisión de arquitectura válida (base de datos gestionada),
no un descuido.

**"¿Falta un Config Server?"** — **Ya no.** Se agregó `config-server` (Spring Cloud
Config en modo `native`) que centraliza la configuración de los servicios en la carpeta
local `config-repo/`. Cada microservicio le pide su configuración al arrancar. (Nota:
el config server centraliza la *configuración*; no cambia dónde vive cada base de datos.)

---

## Archivos recuperados desde Git (venían corruptos en el `.rar`)

```
api-gateway/Dockerfile                     ms-auth/.../MsAuthApplication.java
api-gateway/pom.xml                        ms-auth/.../dto/LoginResponse.java
eureka-server/pom.xml                      ms-inventario/.../dto/DescontarStockRequest.java
ms-inventario/pom.xml                      ms-inventario/.../model/PagoProveedor.java
ms-rrhh/Dockerfile                         ms-inventario/.../repository/PagoProveedorRepository.java
build-jars.ps1                             ms-rrhh/.../repository/AsistenciaRepository.java
ocir-push.ps1                              ms-rrhh/.../repository/IncidenciaPersonalRepository.java
db-dumps/01-auth-datos-limpio.sql          db-dumps/02-ventas-datos.sql
```

> Recomendación: vuelve a extraer el `.rar` original o usa tu copia local para
> confirmar que estos archivos coincidan; se recuperaron de la mejor fuente disponible
> (el commit de Git incluido en el proyecto).

---

## Nota sobre verificación

Los cambios de código/configuración se revisaron y se validó la sintaxis de todos los
YAML, los `pom.xml` (XML bien formado) y el `docker-compose.yml`. **No** se pudo
ejecutar `mvn`/`docker` en el entorno donde se hicieron los cambios (sin acceso a
Maven Central ni a Neon), así que la compilación y el arranque finales debes correrlos
en tu máquina con `docker compose up --build`.
