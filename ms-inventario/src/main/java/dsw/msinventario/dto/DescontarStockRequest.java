package dsw.msinventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Body que envia ms-ventas: { "producto": "...", "cantidad": 5 } */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DescontarStockRequest {
    private String producto;
    private Integer cantidad;
}
