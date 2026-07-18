package dsw.msinventario.service;

import dsw.msinventario.dto.DescontarStockRequest;
import dsw.msinventario.dto.DescontarStockResponse;
import dsw.msinventario.dto.ProductoNoEncontradoException;
import dsw.msinventario.dto.StockInsuficienteException;
import dsw.msinventario.model.Inventario;
import dsw.msinventario.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventarioService {

    private final InventarioRepository inventarioRepository;

    @Transactional(readOnly = true)
    public List<Inventario> listar() {
        return inventarioRepository.findAllByOrderByProductoAsc();
    }

    @Transactional(readOnly = true)
    public Inventario obtenerPorId(Long id) {
        return inventarioRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado en inventario"));
    }

    @Transactional(readOnly = true)
    public List<Inventario> listarBajoStock() {
        return inventarioRepository.findBajoStock();
    }

    @Transactional
    public Inventario crear(Inventario inv) {
        if (inv.getStock() == null) inv.setStock(0);
        if (inv.getStockMinimo() == null) inv.setStockMinimo(0);
        return inventarioRepository.save(inv);
    }

    @Transactional
    public Inventario actualizar(Long id, Inventario datos) {
        Inventario inv = obtenerPorId(id);
        if (datos.getProducto() != null)    inv.setProducto(datos.getProducto());
        if (datos.getStock() != null)       inv.setStock(datos.getStock());
        if (datos.getStockMinimo() != null) inv.setStockMinimo(datos.getStockMinimo());
        if (datos.getCategoria() != null)   inv.setCategoria(datos.getCategoria());
        if (datos.getUnidad() != null)      inv.setUnidad(datos.getUnidad());
        if (datos.getPrecioUnitario() != null) inv.setPrecioUnitario(datos.getPrecioUnitario());
        return inventarioRepository.save(inv);
    }

    @Transactional
    public void eliminar(Long id) {
        inventarioRepository.deleteById(id);
    }

    /** Reposicion / entrada manual de stock (suma). */
    @Transactional
    public Inventario reponerStock(Long id, Integer cantidad) {
        if (cantidad == null || cantidad <= 0)
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        Inventario inv = obtenerPorId(id);
        inv.setStock((inv.getStock() == null ? 0 : inv.getStock()) + cantidad);
        return inventarioRepository.save(inv);
    }

    /**
     * ===== CONTRATO CON ms-ventas (NO CAMBIAR SIN AVISAR) =====
     *
     *  PATCH /inventario/descontar-stock
     *  Body:  { "producto": "Varilla corrugada 1/2\"", "cantidad": 5 }
     *
     *  200 OK          -> { "producto": "...", "stockNuevo": 195, "bajoStock": false }
     *  400 Bad Request -> { "error": "Stock insuficiente. Stock actual: 3" }
     *  400 Bad Request -> { "error": "Producto no encontrado en inventario" }
     *
     * Lo llama PedidoService (ms-ventas) al aprobar un pedido, via Eureka + RestTemplate.
     */
    @Transactional
    public DescontarStockResponse descontarStock(DescontarStockRequest req) {

        if (req.getProducto() == null || req.getProducto().isBlank())
            throw new ProductoNoEncontradoException("Producto no encontrado en inventario");

        if (req.getCantidad() == null || req.getCantidad() <= 0)
            throw new StockInsuficienteException("La cantidad a descontar debe ser mayor a 0");

        // Bloqueo pesimista: si dos pedidos se aprueban a la vez, no se pisan el stock
        Inventario inv = inventarioRepository
                .findByProductoNombreForUpdate(req.getProducto())
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado en inventario"));

        int stockActual = (inv.getStock() == null ? 0 : inv.getStock());

        if (stockActual < req.getCantidad())
            throw new StockInsuficienteException("Stock insuficiente. Stock actual: " + stockActual);

        int stockNuevo = stockActual - req.getCantidad();
        inv.setStock(stockNuevo);
        inventarioRepository.save(inv);

        boolean bajoStock = stockNuevo <= (inv.getStockMinimo() == null ? 0 : inv.getStockMinimo());

        log.info("Stock descontado: producto='{}' cantidad={} stock {} -> {} (bajoStock={})",
                inv.getProducto(), req.getCantidad(), stockActual, stockNuevo, bajoStock);

        return new DescontarStockResponse(inv.getProducto(), stockNuevo, bajoStock);
    }

    /**
     * ===== FASE 4: TRANSACCION COMPENSATORIA (patron Saga) =====
     *
     *  PATCH /inventario/reponer-stock
     *  Body:  { "producto": "Varilla corrugada 1/2\"", "cantidad": 5 }
     *
     * Es el "deshacer" de descontarStock(). La llama ms-ventas (InventarioClient)
     * cuando, tras descontar el stock al aprobar un pedido, falla al guardar el
     * pedido. Sin esto, el stock quedaba descontado por un pedido inexistente.
     *
     * Usa el mismo bloqueo pesimista que el descuento para no pisarse con
     * descuentos concurrentes.
     */
    @Transactional
    public DescontarStockResponse reponerStockPorProducto(DescontarStockRequest req) {

        if (req.getProducto() == null || req.getProducto().isBlank())
            throw new ProductoNoEncontradoException("Producto no encontrado en inventario");

        if (req.getCantidad() == null || req.getCantidad() <= 0)
            throw new StockInsuficienteException("La cantidad a reponer debe ser mayor a 0");

        Inventario inv = inventarioRepository
                .findByProductoNombreForUpdate(req.getProducto())
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado en inventario"));

        int stockActual = (inv.getStock() == null ? 0 : inv.getStock());
        int stockNuevo = stockActual + req.getCantidad();
        inv.setStock(stockNuevo);
        inventarioRepository.save(inv);

        boolean bajoStock = stockNuevo <= (inv.getStockMinimo() == null ? 0 : inv.getStockMinimo());

        log.info("Stock repuesto (compensacion Saga): producto='{}' cantidad={} stock {} -> {}",
                inv.getProducto(), req.getCantidad(), stockActual, stockNuevo);

        return new DescontarStockResponse(inv.getProducto(), stockNuevo, bajoStock);
    }
}
