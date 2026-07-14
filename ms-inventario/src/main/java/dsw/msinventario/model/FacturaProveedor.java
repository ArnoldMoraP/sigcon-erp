package dsw.msinventario.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Tabla real: compras.factura_proveedor
 * Columnas: id, proveedor_id, compra_id, numero_factura, monto, fecha_emision,
 *           fecha_vencimiento, estado, usuario_id, created_at
 * OJO: el numero se llama numero_factura, y el importe es 'monto' (no subtotal/igv/total).
 */
@Data
@Entity
@Table(name = "factura_proveedor")
public class FacturaProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("proveedor_id")
    @Column(name = "proveedor_id")
    private Long proveedorId;

    @JsonProperty("compra_id")
    @Column(name = "compra_id")
    private Long compraId;

    @JsonProperty("numero_factura")
    @Column(name = "numero_factura")
    private String numeroFactura;

    private BigDecimal monto;

    @JsonProperty("fecha_emision")
    @Column(name = "fecha_emision")
    private LocalDate fechaEmision;

    @JsonProperty("fecha_vencimiento")
    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    private String estado;

    @JsonProperty("usuario_id")
    @Column(name = "usuario_id")
    private Long usuarioId;

    @JsonProperty("created_at")
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
