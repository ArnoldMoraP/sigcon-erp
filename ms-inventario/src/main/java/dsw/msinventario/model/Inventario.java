package dsw.msinventario.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Tabla real: almacen.inventario
 * Columnas: id, producto, categoria, stock, stock_minimo, precio_unitario, unidad, fecha_actualizacion
 */
@Data
@Entity
@Table(name = "inventario")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String producto;

    private String categoria;

    @Column(nullable = false)
    private Integer stock;

    @JsonProperty("stock_minimo")
    @Column(name = "stock_minimo")
    private Integer stockMinimo;

    @JsonProperty("precio_unitario")
    @Column(name = "precio_unitario")
    private BigDecimal precioUnitario;

    private String unidad;

    @JsonProperty("fecha_actualizacion")
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Transient
    @JsonProperty("bajo_stock")
    public boolean isBajoStock() {
        if (stock == null) return false;
        int min = (stockMinimo == null ? 0 : stockMinimo);
        return stock <= min;
    }

    @PreUpdate
    @PrePersist
    protected void tocar() {
        fechaActualizacion = LocalDateTime.now();
    }
}
