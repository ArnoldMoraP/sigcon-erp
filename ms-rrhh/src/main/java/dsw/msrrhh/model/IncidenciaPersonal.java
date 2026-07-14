package dsw.msrrhh.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

/** Tabla: rrhh.incidencia_personal (MySQL) */
@Data
@Entity
@Table(name = "incidencia_personal")
public class IncidenciaPersonal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("empleado_id")
    @Column(name = "empleado_id", nullable = false)
    private Long empleadoId;

    /** TARDANZA / FALTA / PERMISO / ACCIDENTE / AMONESTACION */
    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(length = 300)
    private String descripcion;

    @Column(nullable = false)
    private LocalDate fecha;

    /** LEVE / MODERADA / GRAVE */
    @Column(length = 20)
    private String gravedad = "LEVE";

    /** ABIERTA / CERRADA */
    @Column(length = 20)
    private String estado = "ABIERTA";
}
