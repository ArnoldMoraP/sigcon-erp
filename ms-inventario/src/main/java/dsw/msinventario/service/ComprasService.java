package dsw.msinventario.service;

import dsw.msinventario.model.*;
import dsw.msinventario.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Logica del schema "compras": proveedores, ordenes de compra, compras,
 * facturas de proveedor, pagos y presupuestos.
 *
 * Las entidades reflejan las columnas REALES de Neon (verificadas con
 * information_schema). Nota: orden_compra y compra NO tienen subtotal/igv,
 * solo 'total'; factura_proveedor usa 'numero_factura' y 'monto'.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ComprasService {

    private final ProveedorRepository proveedorRepository;
    private final OrdenCompraRepository ordenCompraRepository;
    private final CompraRepository compraRepository;
    private final FacturaProveedorRepository facturaProveedorRepository;
    private final PagoProveedorRepository pagoProveedorRepository;
    private final PresupuestoRepository presupuestoRepository;
    private final InventarioRepository inventarioRepository;

    /** Correlativo calculado en Java (portable). */
    private String siguienteCodigo(String prefijo, List<String> existentes) {
        int max = 0;
        for (String c : existentes) {
            if (c == null || !c.startsWith(prefijo + "-")) continue;
            try {
                max = Math.max(max, Integer.parseInt(c.substring(prefijo.length() + 1).trim()));
            } catch (NumberFormatException ignored) { }
        }
        return String.format("%s-%04d", prefijo, max + 1);
    }

    /** compras.orden_compra.cantidad es texto ("30 TM"); compra.cantidad si es numerica. */
    private int extraerCantidad(String cantidadTexto) {
        if (cantidadTexto == null) return 0;
        StringBuilder digitos = new StringBuilder();
        for (char ch : cantidadTexto.trim().toCharArray()) {
            if (Character.isDigit(ch)) digitos.append(ch);
            else if (digitos.length() > 0) break;   // corta en el primer no-digito tras el numero
        }
        if (digitos.length() == 0) return 0;
        try {
            return Integer.parseInt(digitos.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // ---------------- PROVEEDOR ----------------

    @Transactional(readOnly = true)
    public List<Proveedor> listarProveedores() { return proveedorRepository.findAll(); }

    @Transactional(readOnly = true)
    public Proveedor obtenerProveedor(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado: " + id));
    }

    @Transactional
    public Proveedor crearProveedor(Proveedor p) {
        if (p.getEstado() == null) p.setEstado("ACTIVO");
        return proveedorRepository.save(p);
    }

    @Transactional
    public Proveedor actualizarProveedor(Long id, Proveedor datos) {
        Proveedor p = obtenerProveedor(id);
        if (datos.getNombre() != null)       p.setNombre(datos.getNombre());
        if (datos.getOrigen() != null)       p.setOrigen(datos.getOrigen());
        if (datos.getRuc() != null)          p.setRuc(datos.getRuc());
        if (datos.getContacto() != null)     p.setContacto(datos.getContacto());
        if (datos.getTelefono() != null)     p.setTelefono(datos.getTelefono());
        if (datos.getCategoria() != null)    p.setCategoria(datos.getCategoria());
        if (datos.getCalificacion() != null) p.setCalificacion(datos.getCalificacion());
        if (datos.getEstado() != null)       p.setEstado(datos.getEstado());
        return proveedorRepository.save(p);
    }

    @Transactional
    public void eliminarProveedor(Long id) { proveedorRepository.deleteById(id); }

    // ---------------- ORDEN DE COMPRA ----------------

    @Transactional(readOnly = true)
    public List<OrdenCompra> listarOrdenes() { return ordenCompraRepository.findAllByOrderByIdDesc(); }

    @Transactional(readOnly = true)
    public OrdenCompra obtenerOrden(Long id) {
        return ordenCompraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden de compra no encontrada: " + id));
    }

    @Transactional
    public OrdenCompra crearOrden(OrdenCompra oc) {
        List<String> codigos = ordenCompraRepository.findAll().stream()
                .map(OrdenCompra::getCodigo).toList();
        if (oc.getCodigo() == null || oc.getCodigo().isBlank())
            oc.setCodigo(siguienteCodigo("OC", codigos));
        if (oc.getEstado() == null) oc.setEstado("PENDIENTE");
        if (oc.getFechaEntrega() == null) oc.setFechaEntrega(LocalDate.now().plusDays(7));
        return ordenCompraRepository.save(oc);
    }

    @Transactional
    public OrdenCompra cambiarEstadoOrden(Long id, String estado) {
        OrdenCompra oc = obtenerOrden(id);
        oc.setEstado(estado.toUpperCase());
        return ordenCompraRepository.save(oc);
    }

    // ---------------- COMPRA (recepcion de la orden) ----------------

    @Transactional(readOnly = true)
    public List<Compra> listarCompras() { return compraRepository.findAllByOrderByIdDesc(); }

    @Transactional(readOnly = true)
    public Compra obtenerCompra(Long id) {
        return compraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada: " + id));
    }

    /**
     * Recibe una orden de compra APROBADA -> registra la compra y SUMA el stock
     * en almacen.inventario (contraparte del descuento que hace ms-ventas).
     */
    @Transactional
    public Compra registrarDesdeOrden(Long ordenCompraId) {
        OrdenCompra oc = obtenerOrden(ordenCompraId);

        String est = String.valueOf(oc.getEstado());
        if (!"APROBADA".equalsIgnoreCase(est) && !"APROBADO".equalsIgnoreCase(est))
            throw new RuntimeException("Solo se pueden recibir ordenes APROBADAS. Estado actual: " + est);

        if (!compraRepository.findByOrdenCompraId(ordenCompraId).isEmpty())
            throw new RuntimeException("Ya existe una compra registrada para la orden: " + oc.getCodigo());

        // 'cantidad' en orden_compra es texto con unidad (ej. "30 TM"): extraemos el numero
        int unidades = extraerCantidad(oc.getCantidad());
        if (unidades <= 0)
            throw new RuntimeException("No pude interpretar la cantidad de la orden: " + oc.getCantidad());

        // Entrada de mercaderia: suma stock (o crea el producto si no existe)
        Inventario inv = inventarioRepository.findByProductoNombre(oc.getProducto()).orElseGet(() -> {
            Inventario nuevo = new Inventario();
            nuevo.setProducto(oc.getProducto());
            nuevo.setStock(0);
            nuevo.setStockMinimo(0);
            return inventarioRepository.save(nuevo);
        });
        inv.setStock((inv.getStock() == null ? 0 : inv.getStock()) + unidades);
        inventarioRepository.save(inv);

        // precio unitario derivado del total de la orden (orden_compra no guarda precio_unitario)
        BigDecimal precioUnitario = BigDecimal.ZERO;
        if (oc.getTotal() != null && unidades > 0)
            precioUnitario = oc.getTotal().divide(BigDecimal.valueOf(unidades), 2, RoundingMode.HALF_UP);

        Compra c = new Compra();
        c.setOrdenCompraId(oc.getId());
        c.setProveedorId(oc.getProveedorId());
        c.setInventarioId(inv.getId());
        c.setProducto(oc.getProducto());
        c.setCantidad(unidades);
        c.setPrecioUnitario(precioUnitario);
        c.setTotal(oc.getTotal());
        c.setFechaCompra(LocalDateTime.now());
        c.setUsuarioId(oc.getUsuarioId());
        Compra guardada = compraRepository.save(c);

        oc.setEstado("RECIBIDA");
        ordenCompraRepository.save(oc);

        log.info("Compra {} registrada desde orden {} (+{} de stock en '{}')",
                guardada.getId(), oc.getCodigo(), oc.getCantidad(), oc.getProducto());
        return guardada;
    }

    // ---------------- FACTURA PROVEEDOR ----------------

    @Transactional(readOnly = true)
    public List<FacturaProveedor> listarFacturas() { return facturaProveedorRepository.findAllByOrderByIdDesc(); }

    @Transactional(readOnly = true)
    public FacturaProveedor obtenerFactura(Long id) {
        return facturaProveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura de proveedor no encontrada: " + id));
    }

    @Transactional
    public FacturaProveedor crearFactura(FacturaProveedor f) {
        if (f.getEstado() == null) f.setEstado("PENDIENTE");
        if (f.getFechaEmision() == null) f.setFechaEmision(LocalDate.now());
        return facturaProveedorRepository.save(f);
    }

    /** Emite la factura a partir de una compra ya registrada. */
    @Transactional
    public FacturaProveedor emitirDesdeCompra(Long compraId, String numero) {
        Compra c = obtenerCompra(compraId);
        if (!facturaProveedorRepository.findByCompraId(compraId).isEmpty())
            throw new RuntimeException("Ya existe una factura para la compra: " + c.getId());

        FacturaProveedor f = new FacturaProveedor();
        f.setNumeroFactura(numero != null && !numero.isBlank() ? numero : "F-" + c.getId());
        f.setCompraId(c.getId());
        f.setProveedorId(c.getProveedorId());
        f.setMonto(c.getTotal());
        f.setEstado("PENDIENTE");
        f.setFechaEmision(LocalDate.now());
        f.setFechaVencimiento(LocalDate.now().plusDays(30));
        f.setUsuarioId(c.getUsuarioId());
        return facturaProveedorRepository.save(f);
    }

    // ---------------- PAGO PROVEEDOR ----------------

    @Transactional(readOnly = true)
    public List<PagoProveedor> listarPagos() { return pagoProveedorRepository.findAllByOrderByIdDesc(); }

    @Transactional
    public PagoProveedor registrarPago(PagoProveedor pago) {
        if (pago.getFacturaId() == null)
            throw new RuntimeException("El pago debe estar asociado a una factura (factura_id)");

        FacturaProveedor f = obtenerFactura(pago.getFacturaId());
        if ("PAGADA".equalsIgnoreCase(f.getEstado()))
            throw new RuntimeException("La factura " + f.getNumeroFactura() + " ya fue pagada");

        if (pago.getMonto() == null)      pago.setMonto(f.getMonto());
        if (pago.getMetodoPago() == null) pago.setMetodoPago("TRANSFERENCIA");
        if (pago.getFechaPago() == null)  pago.setFechaPago(LocalDate.now());
        pago.setProveedorId(f.getProveedorId());
        PagoProveedor guardado = pagoProveedorRepository.save(pago);

        f.setEstado("PAGADA");
        facturaProveedorRepository.save(f);

        log.info("Pago registrado para factura {} por {}", f.getNumeroFactura(), pago.getMonto());
        return guardado;
    }

    // ---------------- PRESUPUESTO ----------------

    @Transactional(readOnly = true)
    public List<Presupuesto> listarPresupuestos() { return presupuestoRepository.findAllByOrderByIdDesc(); }

    @Transactional(readOnly = true)
    public Presupuesto obtenerPresupuesto(Long id) {
        return presupuestoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Presupuesto no encontrado: " + id));
    }

    @Transactional
    public Presupuesto crearPresupuesto(Presupuesto p) {
        if (p.getMontoDisponible() == null) p.setMontoDisponible(p.getMontoTotal());
        return presupuestoRepository.save(p);
    }

    /** Ejecuta (gasta) un monto: descuenta de monto_disponible. */
    @Transactional
    public Presupuesto ejecutarPresupuesto(Long id, BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("El monto debe ser mayor a 0");

        Presupuesto p = obtenerPresupuesto(id);
        BigDecimal disponible = (p.getMontoDisponible() == null ? BigDecimal.ZERO : p.getMontoDisponible());

        if (monto.compareTo(disponible) > 0)
            throw new RuntimeException("El gasto excede el presupuesto. Disponible: " + disponible);

        p.setMontoDisponible(disponible.subtract(monto));
        return presupuestoRepository.save(p);
    }

    @Transactional
    public void eliminarPresupuesto(Long id) { presupuestoRepository.deleteById(id); }
}