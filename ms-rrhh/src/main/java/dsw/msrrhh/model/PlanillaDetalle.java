package dsw.msrrhh.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

/** Tabla: rrhh.planilla_detalle (MySQL) */
@Data
@Entity
@Table(name = "planilla_detalle")
public class PlanillaDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("planilla_id")
    @Column(name = "planilla_id", nullable = false)
    private Long planillaId;

    @JsonProperty("empleado_id")
    @Column(name = "empleado_id", nullable = false)
    private Long empleadoId;

    @JsonProperty("sueldo_base")
    @Column(name = "sueldo_base", precision = 12, scale = 2)
    private BigDecimal sueldoBase;

    @Column(precision = 12, scale = 2)
    private BigDecimal bonificaciones;

    @Column(precision = 12, scale = 2)
    private BigDecimal descuentos;

    @Column(precision = 12, scale = 2)
    private BigDecimal neto;
}
