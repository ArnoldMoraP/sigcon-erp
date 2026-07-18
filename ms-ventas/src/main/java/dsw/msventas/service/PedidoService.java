package dsw.msventas.service;

import dsw.msventas.client.InventarioClient;
import dsw.msventas.model.Cotizacion;
import dsw.msventas.model.Pedido;
import dsw.msventas.repository.CotizacionRepository;
import dsw.msventas.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CotizacionRepository cotizacionRepository;
    private final InventarioClient inventarioClient;

    private static final BigDecimal IGV = new BigDecimal("0.18");

    private String generarCodigo() {
        Integer max = pedidoRepository.findMaxCodigoNumber();
        int sig = (max == null ? 0 : max) + 1;
        return String.format("PED-%04d", sig);
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAllByOrderByFechaRegistroDesc();
    }

    @Transactional(readOnly = true)
    public Pedido obtenerPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + id));
    }

    @Transactional
    public Pedido registrarPedido(Pedido pedido) {
        pedido.setCodigo(generarCodigo());
        pedido.setEstado("PENDIENTE");
        calcularMontos(pedido);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido crearDesdeCotizacion(Long cotizacionId) {
        Cotizacion cot = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada: " + cotizacionId));

        if (!"APROBADA".equalsIgnoreCase(cot.getEstado())) {
            throw new RuntimeException(
                    "Solo cotizaciones APROBADAS pueden convertirse a pedido. Estado actual: "
                    + cot.getEstado());
        }

        Pedido pedido = new Pedido();
        pedido.setCodigo(generarCodigo());
        pedido.setCliente(cot.getCliente());
        pedido.setRuc(cot.getRuc());
        pedido.setProducto(cot.getProducto());
        pedido.setCantidad(cot.getCantidad());
        pedido.setPrecioUnitario(cot.getPrecioUnitario());
        pedido.setEstado("PENDIENTE");
        pedido.setUsuarioId(cot.getUsuarioId());
        calcularMontos(pedido);

        Pedido guardado = pedidoRepository.save(pedido);

        cot.setEstado("CONVERTIDA");
        cotizacionRepository.save(cot);

        log.info("Pedido {} creado desde cotización {}", guardado.getCodigo(), cot.getCodigo());
        return guardado;
    }

    @Transactional
    public Map<String, Object> actualizarEstadoConResultado(Long id, String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + id));

        String estadoUp = nuevoEstado.toUpperCase();
        Map<String, Object> resultado = new HashMap<>();

        boolean stockDescontado = false;
        if ("APROBADO".equals(estadoUp)) {
            // Llamada REST a ms-inventario en vez de acceso directo a la BD.
            // OJO: esto se confirma en una transaccion APARTE dentro de ms-inventario.
            Map<String, Object> respuestaInventario =
                    inventarioClient.descontarStock(pedido.getProducto(), pedido.getCantidad());
            stockDescontado = true;
            resultado.put("inventario", respuestaInventario);
            log.info("Stock descontado vía ms-inventario para producto '{}'", pedido.getProducto());
        }

        pedido.setEstado(estadoUp);

        // FASE 4 (compensacion Saga):
        // Usamos saveAndFlush (no save) para forzar el INSERT/UPDATE AHORA y que
        // cualquier error de BD salte DENTRO de este try, no despues al hacer commit.
        // Si el pedido no se puede guardar despues de haber descontado stock,
        // deshacemos el descuento para no dejar inventario "fantasma" descontado
        // por un pedido que finalmente no existe.
        try {
            Pedido guardado = pedidoRepository.saveAndFlush(pedido);
            resultado.put("pedido", guardado);
            return resultado;
        } catch (RuntimeException e) {
            if (stockDescontado) {
                log.error("Fallo al guardar el pedido {} tras descontar stock. Compensando (reponiendo stock)...",
                        pedido.getCodigo());
                inventarioClient.reponerStock(pedido.getProducto(), pedido.getCantidad());
            }
            throw e;
        }
    }

    private void calcularMontos(Pedido pedido) {
        if (pedido.getPrecioUnitario() == null || pedido.getCantidad() == null) return;
        BigDecimal subtotal = pedido.getPrecioUnitario()
                .multiply(new BigDecimal(pedido.getCantidad()))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal igv = subtotal.multiply(IGV).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(igv).setScale(2, RoundingMode.HALF_UP);
        pedido.setSubtotal(subtotal);
        pedido.setIgv(igv);
        pedido.setTotal(total);
    }
}