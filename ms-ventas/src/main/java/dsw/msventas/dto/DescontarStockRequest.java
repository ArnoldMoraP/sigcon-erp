package dsw.msventas.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DescontarStockRequest {
    private String producto;
    private Integer cantidad;
}