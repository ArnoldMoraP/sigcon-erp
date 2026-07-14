# ms-inventario + ms-rrhh — cómo levantarlos y probarlos

> Estos dos microservicios completan los 4 del proyecto. Con esto ya se cumple el
> requisito de la profesora: **PostgreSQL** (ms-auth, ms-ventas, ms-inventario) +
> **MySQL** (ms-rrhh) = 2 motores de BD distintos.

---

## ⚠️ Antes de nada: verificar las columnas reales de `compras` y `almacen`

El RAR que me pasaste **no incluye el monolito ni el `pg_dump`**, así que los nombres de
columna de las 7 tablas de `compras`/`almacen` los inferí. Las entidades de `ms-rrhh` sí son
exactas, porque el schema MySQL lo creamos nosotros (`ms-rrhh/db/init-mysql.sql`).

Ejecuta `ms-inventario/schema-check.sql` en el SQL Editor de Neon y compara con las entidades.
Si una columna no coincide, el fix es de una línea: cambiar el `@Column(name = "...")` o borrar
el campo. Hibernate **solo** lee las columnas mapeadas, así que columnas de más en la tabla real
no rompen nada; el problema es solo al revés (campo mapeado que no existe).

Las entidades mapean a propósito **pocas columnas** para minimizar ese riesgo:
`Inventario` solo usa `id`, `producto`, `stock`, `stock_minimo` (los tres que necesita el flujo de
venta). Dentro del archivo hay un bloque comentado con las columnas opcionales típicas
(`categoria`, `unidad_medida`, `precio_unitario`, `estado`) por si existen y las quieren exponer.

---

## 1. ms-inventario (puerto 8083 · PostgreSQL · schemas `compras` + `almacen`)

- **Dos schemas, sin `default_schema` global**: cada entidad declara el suyo con
  `@Table(schema = "compras")` / `@Table(schema = "almacen")`, tal como pide el plan.
- `ddl-auto: none`.
- `run-ms-inventario.ps1` usa las **mismas credenciales de Neon** que ms-auth/ms-ventas, pero
  **sin `currentSchema=`** en la URL (justamente porque son dos schemas).

### El endpoint que desbloquea a ms-ventas

```
PATCH /inventario/descontar-stock
Body:  { "producto": "Varilla corrugada 1/2\"", "cantidad": 5 }

200 OK          → { "producto": "...", "stockNuevo": 195, "bajoStock": false }
400 Bad Request → { "error": "Stock insuficiente. Stock actual: 3" }
400 Bad Request → { "error": "Producto no encontrado en inventario" }
```

Implementado **exactamente** con esos nombres de campo (`stockNuevo`, `bajoStock` en camelCase)
y con `GlobalExceptionHandler` devolviendo `{ "error": "..." }` con HTTP 400, que es la forma que
`InventarioClient` de ms-ventas espera leer en `HttpClientErrorException.BadRequest`.

Detalles: el match del producto es **case-insensitive y con trim** (para que no falle por un
espacio de más entre `comercial.pedido.producto` y `almacen.inventario.producto`), y usa
**bloqueo pesimista** (`PESSIMISTIC_WRITE`) para que dos pedidos aprobados a la vez no pisen el stock.

### Otros endpoints

| Recurso | Rutas |
|---|---|
| Inventario | `GET /inventario`, `GET /inventario/bajo-stock`, `GET/POST/PUT/DELETE /inventario/{id}`, `PATCH /inventario/{id}/reponer` |
| Proveedores | `GET/POST/PUT/DELETE /proveedores` |
| Órdenes de compra | `GET/POST /ordenes-compra`, `PATCH /ordenes-compra/{id}/estado` |
| Compras | `GET /compras`, `POST /compras/desde-orden/{ordenCompraId}` → **suma stock al inventario** |
| Facturas de proveedor | `GET/POST /facturas-proveedor`, `POST /facturas-proveedor/desde-compra/{compraId}` |
| Pagos de proveedor | `GET/POST /pagos-proveedor` |
| Presupuestos | `GET/POST/DELETE /presupuestos`, `PATCH /presupuestos/{id}/ejecutar` |

El flujo de compras queda simétrico al de ventas:
`orden de compra → aprobar → recibir (entra stock) → factura → pago`.

---

## 2. ms-rrhh (puerto 8084 · MySQL en contenedor)

**Primero levanta MySQL** (la BD nace vacía; el `init.sql` crea las 6 tablas y mete 5 empleados
de prueba para la demo):

```powershell
cd ms-rrhh
docker compose -f docker-compose-mysql.yml up -d
docker logs -f mysql-rrhh    # esperar "ready for connections"
.\run-ms-rrhh.ps1
```

> Si ya habías creado el volumen antes, el `init.sql` **no** se vuelve a ejecutar.
> Para forzarlo: `docker compose -f docker-compose-mysql.yml down -v` y volver a levantar.

Sobre las **queries nativas de Postgres** que el plan advertía: en `ms-rrhh` **no hay ninguna**.
Todo es Spring Data / JPQL, y los correlativos (`EMP-0001`, `PLA-0001`) se calculan en Java, así
que no hay `~`, ni `CAST(... AS INTEGER)`, ni schemas calificados que traducir. El driver es
`mysql-connector-j` y el dialecto `MySQLDialect`.

### Endpoints (todos bajo `/rrhh/*`, para que el gateway necesite una sola ruta)

| Recurso | Rutas |
|---|---|
| Empleados | `GET /rrhh/empleados`, `/activos`, `GET/POST/PUT/DELETE /rrhh/empleados/{id}`, `PATCH /{id}/desactivar` |
| Asistencias | `GET /rrhh/asistencias`, `POST /rrhh/asistencias`, `PATCH /{id}/salida`, `GET /empleado/{id}` |
| Planillas | `GET /rrhh/planillas`, `POST /rrhh/planillas/generar`, `PATCH /{id}/aprobar`, `POST /{id}/pagar`, `GET /rrhh/planillas/pagos` |
| Incidencias | `GET /rrhh/incidencias`, `POST`, `PATCH /{id}/cerrar` |

Lógica de negocio real (no CRUD pelado): al registrar asistencia después de las 08:10 se marca
**TARDANZA** y se genera automáticamente la **incidencia**; al generar la planilla del periodo se
toman los empleados ACTIVOS, se descuenta 13% de ley + S/20 por incidencia abierta del mes, y se
llena `planilla_detalle`. `planilla → aprobar → pagar` deja el registro en `pago_planilla`.

---

## 3. Gateway

Ya le agregué las rutas a `api-gateway/src/main/resources/application.yml`:

```yaml
- id: ms-inventario
  uri: lb://ms-inventario
  predicates:
    - Path=/inventario/**,/proveedores/**,/ordenes-compra/**,/compras/**,/facturas-proveedor/**,/pagos-proveedor/**,/presupuestos/**

- id: ms-rrhh
  uri: lb://ms-rrhh
  predicates:
    - Path=/rrhh/**
```

---

## 4. Orden de arranque y prueba end-to-end

```
1) eureka-server        2) ms-auth        3) ms-ventas
4) ms-inventario        5) ms-rrhh        6) api-gateway
```

Verifica en http://localhost:8761 que aparezcan las 5 apps + gateway.

Flujo completo por Postman (con el JWT del login, vía `http://localhost:8080`):

1. `POST /auth/login` → token
2. `GET /inventario` → anota un `producto` y su `stock`
3. `POST /cotizaciones` → usa **ese mismo texto de producto**
4. `PATCH /cotizaciones/{id}/estado` → `{"estado":"APROBADA"}`
5. `POST /cotizaciones/{id}/convertir-pedido`
6. `PATCH /pedidos/{id}/estado` → `{"estado":"APROBADO"}` → **acá ms-ventas llama a ms-inventario
   y el stock baja de verdad** (la respuesta trae el bloque `inventario`)
7. `GET /inventario` → confirmar el stock descontado (captura para la entrega)
8. `POST /ventas/desde-pedido/{pedidoId}` → `PATCH /ventas/{id}/estado` `{"estado":"APROBADO"}`
9. `POST /despacho/desde-venta/{ventaId}`
10. `GET /rrhh/empleados` y `POST /rrhh/planillas/generar` `{"periodo":"2026-07"}`

Prueba negativa que vale la pena capturar: aprobar un pedido con cantidad mayor al stock →
`400 { "error": "Stock insuficiente. Stock actual: N" }`. Demuestra que la comunicación
REST entre microservicios funciona en ambos sentidos.

---

## 5. Lo que queda

- Día 3: `Dockerfile` de los 6 servicios (los de estos dos ya están) + `docker-compose.yml` raíz
- Día 4: GKE, `StatefulSet` + `PVC` para MySQL, `Secret` con `JWT_SECRET` y credenciales
- `run-*.ps1` ya está cubierto por el `.gitignore` (no se commitean credenciales)
