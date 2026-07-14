package dsw.msrrhh.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Tabla: rrhh.empleado (MySQL) */
@Data
@Entity
@Table(name = "empleado")
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(nullable = false, unique = true, length = 15)
    private String dni;

    @Column(length = 100)
    private String cargo;

    @Column(length = 100)
    private String area;

    @Column(precision = 12, scale = 2)
    private BigDecimal salario;

    @JsonProperty("fecha_ingreso")
    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    @Column(length = 150)
    private String email;

    @Column(length = 20)
    private String telefono;

    @Column(nullable = false, length = 20)
    private String estado = "ACTIVO";

    @JsonProperty("usuario_id")
    @Column(name = "usuario_id")
    private Long usuarioId;
}
