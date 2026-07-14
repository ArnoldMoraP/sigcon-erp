package dsw.msinventario.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta 200 del contrato con ms-ventas:
 * { "producto": "...", "stockNuevo": 195, "bajoStock": false }
 *
 * OJO: los nombres de campo son camelCase EXACTOS (stockNuevo / bajoStock).
 * No renombrar sin avisar a ms-ventas (InventarioClient los lee tal cual).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DescontarStockResponse {

    private String producto;

    @JsonProperty("stockNuevo")
    private Integer stockNuevo;

    @JsonProperty("bajoStock")
    private Boolean bajoStock;
}
