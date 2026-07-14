package dsw.msinventario.controller;

import dsw.msinventario.model.FacturaProveedor;
import dsw.msinventario.service.ComprasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/facturas-proveedor")
@RequiredArgsConstructor
public class FacturaProveedorController {

    private final ComprasService comprasService;

    @GetMapping
    public ResponseEntity<List<FacturaProveedor>> listar() {
        return ResponseEntity.ok(comprasService.listarFacturas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacturaProveedor> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(comprasService.obtenerFactura(id));
    }

    @PostMapping
    public ResponseEntity<FacturaProveedor> crear(@RequestBody FacturaProveedor f) {
        return ResponseEntity.status(HttpStatus.CREATED).body(comprasService.crearFactura(f));
    }

    /** Body opcional: { "numero": "F001-000123" } */
    @PostMapping("/desde-compra/{compraId}")
    public ResponseEntity<FacturaProveedor> emitirDesdeCompra(@PathVariable Long compraId,
                                                              @RequestBody(required = false) Map<String, String> body) {
        String numero = (body == null ? null : body.get("numero"));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(comprasService.emitirDesdeCompra(compraId, numero));
    }
}
