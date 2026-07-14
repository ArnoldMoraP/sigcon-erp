package dsw.msrrhh.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/** Tabla: rrhh.asistencia (MySQL) */
@Data
@Entity
@Table(name = "asistencia")
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("empleado_id")
    @Column(name = "empleado_id", nullable = false)
    private Long empleadoId;

    @Column(nullable = false)
    private LocalDate fecha;

    @JsonProperty("hora_entrada")
    @Column(name = "hora_entrada")
    private LocalTime horaEntrada;

    @JsonProperty("hora_salida")
    @Column(name = "hora_salida")
    private LocalTime horaSalida;

    @JsonProperty("horas_trabajadas")
    @Column(name = "horas_trabajadas", precision = 5, scale = 2)
    private BigDecimal horasTrabajadas;

    /** PUNTUAL / TARDANZA / FALTA */
    @Column(nullable = false, length = 20)
    private String estado = "PUNTUAL";

    @Column(length = 255)
    private String observacion;
}
