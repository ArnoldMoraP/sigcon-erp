package dsw.msinventario.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * Tabla real: compras.proveedor
 * Columnas: id, nombre, origen, ruc, contacto, telefono, categoria, calificacion, estado, created_at, updated_at
 */
@Data
@Entity
@Table(name = "proveedor")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String origen;
    private String ruc;
    private String contacto;
    private String telefono;
    private String categoria;
    private Integer calificacion;
    private String estado;

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
