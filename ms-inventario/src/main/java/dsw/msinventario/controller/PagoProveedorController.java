package dsw.msinventario.controller;

import dsw.msinventario.model.PagoProveedor;
import dsw.msinventario.service.ComprasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagos-proveedor")
@RequiredArgsConstructor
public class PagoProveedorController {

    private final ComprasService comprasService;

    @GetMapping
    public ResponseEntity<List<PagoProveedor>> listar() {
        return ResponseEntity.ok(comprasService.listarPagos());
    }

    /** Body: { "factura_id": 1, "monto": 1180.00, "metodo_pago": "TRANSFERENCIA" } */
    @PostMapping
    public ResponseEntity<PagoProveedor> registrar(@RequestBody PagoProveedor pago) {
        return ResponseEntity.status(HttpStatus.CREATED).body(comprasService.registrarPago(pago));
    }
}
