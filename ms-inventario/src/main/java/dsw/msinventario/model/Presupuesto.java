package dsw.msinventario.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Tabla real: compras.presupuesto
 * Columnas: id, periodo, monto_total, monto_disponible, fecha_actualizacion
 * OJO: NO tiene codigo, area, descripcion, estado, monto_asignado ni monto_ejecutado.
 */
@Data
@Entity
@Table(name = "presupuesto")
public class Presupuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String periodo;

    @JsonProperty("monto_total")
    @Column(name = "monto_total")
    private BigDecimal montoTotal;

    @JsonProperty("monto_disponible")
    @Column(name = "monto_disponible")
    private BigDecimal montoDisponible;

    @JsonProperty("fecha_actualizacion")
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    @PreUpdate
    protected void tocar() {
        fechaActualizacion = LocalDateTime.now();
    }
}
