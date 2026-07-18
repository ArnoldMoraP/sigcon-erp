-- =====================================================================
--  FASE 1 - Verificacion post-migracion (Neon / PostgreSQL)
--
--  Son 3 BASES FISICAS SEPARADAS (auth_db, ventas_db, inventario_db).
--  No se pueden consultar las 3 en una sola query: hay que conectarse a
--  cada una por separado (en el SQL Editor de Neon, elegir la base arriba)
--  y correr el bloque correspondiente.
--
--  Objetivo: confirmar que las tablas EXISTEN y TIENEN DATOS despues de
--  correr migrar-bases.ps1. Si algun conteo da 0, la migracion de esa
--  tabla no cargo (revisar el log de migrar-bases.ps1).
-- =====================================================================


-- ============================ auth_db ================================
-- Conectarse a: auth_db
-- Esquemas esperados: seguridad (usuario, rol), catalogos (estado), etc.

SELECT 'auth_db' AS base, schemaname AS esquema, relname AS tabla, n_live_tup AS filas_aprox
FROM pg_stat_user_tables
ORDER BY schemaname, relname;

-- Debe existir al menos un usuario para poder loguearse:
SELECT count(*) AS usuarios FROM seguridad.usuario;
-- (opcional) ver los usuarios y su rol:
-- SELECT u.username, r.nombre AS rol FROM seguridad.usuario u
--   LEFT JOIN seguridad.rol r ON r.id = u.rol_id;


-- =========================== ventas_db ===============================
-- Conectarse a: ventas_db
-- Esquemas esperados: comercial (pedido, cotizacion, venta, despacho)

SELECT 'ventas_db' AS base, schemaname AS esquema, relname AS tabla, n_live_tup AS filas_aprox
FROM pg_stat_user_tables
ORDER BY schemaname, relname;

SELECT count(*) AS pedidos      FROM comercial.pedido;
SELECT count(*) AS cotizaciones FROM comercial.cotizacion;


-- ========================= inventario_db =============================
-- Conectarse a: inventario_db
-- Esquemas esperados: almacen (inventario), compras (proveedor, orden_compra, ...)

SELECT 'inventario_db' AS base, schemaname AS esquema, relname AS tabla, n_live_tup AS filas_aprox
FROM pg_stat_user_tables
ORDER BY schemaname, relname;

SELECT count(*) AS productos_inventario FROM almacen.inventario;

-- Comprobacion de columnas inferidas (README-ms-inventario-y-rrhh.md):
-- Comparar esta salida con las @Column de las entidades en
-- ms-inventario/src/main/java/dsw/msinventario/model/*.java
-- Si una columna mapeada en Java NO aparece aca, Hibernate fallara al leerla.
SELECT table_schema, table_name, column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_schema IN ('compras', 'almacen')
ORDER BY table_schema, table_name, ordinal_position;
