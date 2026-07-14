-- =====================================================================
-- Ejecutar esto en el SQL Editor de Neon ANTES de levantar ms-inventario.
-- Compara la salida con las @Column de las entidades en
-- ms-inventario/src/main/java/dsw/msinventario/model/
--
-- Si una columna mapeada NO existe en la tabla real, Hibernate falla al
-- consultarla (SQL: column ... does not exist). La solucion es solo
-- renombrar el @Column(name = "...") o borrar el campo.
-- =====================================================================
SELECT table_schema,
       table_name,
       column_name,
       data_type,
       is_nullable
FROM information_schema.columns
WHERE table_schema IN ('compras', 'almacen')
ORDER BY table_schema, table_name, ordinal_position;

-- Sanity check del flujo de venta: el producto del pedido debe existir
-- (mismo texto) en almacen.inventario, si no, descontar-stock devuelve
-- "Producto no encontrado en inventario".
SELECT DISTINCT p.producto AS producto_en_pedido,
       i.producto          AS producto_en_inventario,
       i.stock
FROM comercial.pedido p
LEFT JOIN almacen.inventario i
       ON LOWER(TRIM(i.producto)) = LOWER(TRIM(p.producto));
