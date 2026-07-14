package dsw.msinventario.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Tabla real: compras.pago_proveedor
 * Columnas: id, factura_id, proveedor_id, monto, metodo_pago, fecha_pago, usuario_id
 * OJO: NO tiene estado.
 */
@Data
@Entity
@Table(name = "pago_proveedor")
public class PagoProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("factura_id")
    @Column(name = "factura_id")
    private Long facturaId;

    @JsonProperty("proveedor_id")
    @Column(name = "proveedor_id")
    private Long proveedorId;

    private BigDecimal monto;

    @JsonProperty("metodo_pago")
    @Column(name = "metodo_pago")
    private String metodoPago;

    @JsonProperty("fecha_pago")
    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    @JsonProperty("usuario_id")
    @Column(name = "usuario_id")
    private Long usuarioId;
}
