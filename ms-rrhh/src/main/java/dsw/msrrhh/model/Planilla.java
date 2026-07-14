package dsw.msrrhh.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Tabla: rrhh.planilla (MySQL) */
@Data
@Entity
@Table(name = "planilla")
public class Planilla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    /** Formato 'YYYY-MM' */
    @Column(nullable = false, length = 7)
    private String periodo;

    @JsonProperty("fecha_generacion")
    @Column(name = "fecha_generacion")
    private LocalDate fechaGeneracion;

    @JsonProperty("total_ingresos")
    @Column(name = "total_ingresos", precision = 14, scale = 2)
    private BigDecimal totalIngresos;

    @JsonProperty("total_descuentos")
    @Column(name = "total_descuentos", precision = 14, scale = 2)
    private BigDecimal totalDescuentos;

    @JsonProperty("total_neto")
    @Column(name = "total_neto", precision = 14, scale = 2)
    private BigDecimal totalNeto;

    /** GENERADA / APROBADA / PAGADA */
    @Column(nullable = false, length = 20)
    private String estado = "GENERADA";

    @JsonProperty("usuario_id")
    @Column(name = "usuario_id")
    private Long usuarioId;
}
