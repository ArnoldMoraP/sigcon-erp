package dsw.msinventario.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Tabla real: compras.orden_compra
 * Columnas: id, codigo, proveedor_id, producto, cantidad, total, fecha_entrega, estado,
 *           usuario_id, created_at, updated_at
 * OJO: NO tiene precio_unitario, ni subtotal, ni igv. Solo 'total'.
 */
@Data
@Entity
@Table(name = "orden_compra")
public class OrdenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;

    @JsonProperty("proveedor_id")
    @Column(name = "proveedor_id")
    private Long proveedorId;

    private String producto;

    /**
     * OJO: en la BD real 'cantidad' NO es numerica: guarda texto con unidad
     * incluida (ej. "30 TM"). Por eso se mapea como String y no como Integer.
     */
    private String cantidad;

    private BigDecimal total;

    @JsonProperty("fecha_entrega")
    @Column(name = "fecha_entrega")
    private LocalDate fechaEntrega;

    private String estado;

    @JsonProperty("usuario_id")
    @Column(name = "usuario_id")
    private Long usuarioId;

    @JsonProperty("created_at")
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void tocar() {
        updatedAt = LocalDateTime.now();
    }
}
