package dsw.msinventario.dto;

import lombok.Data;

/** Body para reponer/ajustar stock manualmente: { "cantidad": 50 } */
@Data
public class AjusteStockRequest {
    private Integer cantidad;
}
