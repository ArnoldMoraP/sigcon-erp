package dsw.msrrhh.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Tabla: rrhh.pago_planilla (MySQL) */
@Data
@Entity
@Table(name = "pago_planilla")
public class PagoPlanilla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("planilla_id")
    @Column(name = "planilla_id", nullable = false)
    private Long planillaId;

    @JsonProperty("fecha_pago")
    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    @Column(precision = 14, scale = 2)
    private BigDecimal monto;

    @JsonProperty("metodo_pago")
    @Column(name = "metodo_pago", length = 30)
    private String metodoPago = "TRANSFERENCIA";

    @Column(length = 20)
    private String estado = "PAGADO";
}
