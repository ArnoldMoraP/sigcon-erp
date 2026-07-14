package dsw.msinventario.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Tabla real: compras.compra
 * Columnas: id, orden_compra_id, proveedor_id, inventario_id, producto, cantidad,
 *           precio_unitario, total, fecha_compra, usuario_id
 * OJO: NO tiene codigo, ni subtotal, ni igv, ni estado.
 */
@Data
@Entity
@Table(name = "compra")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("orden_compra_id")
    @Column(name = "orden_compra_id")
    private Long ordenCompraId;

    @JsonProperty("proveedor_id")
    @Column(name = "proveedor_id")
    private Long proveedorId;

    @JsonProperty("inventario_id")
    @Column(name = "inventario_id")
    private Long inventarioId;

    private String producto;
    private Integer cantidad;

    @JsonProperty("precio_unitario")
    @Column(name = "precio_unitario")
    private BigDecimal precioUnitario;

    private BigDecimal total;

    @JsonProperty("fecha_compra")
    @Column(name = "fecha_compra")
    private LocalDateTime fechaCompra;

    @JsonProperty("usuario_id")
    @Column(name = "usuario_id")
    private Long usuarioId;

    @PrePersist
    protected void onCreate() {
        if (fechaCompra == null) fechaCompra = LocalDateTime.now();
    }
}
