package dsw.msinventario.controller;

import dsw.msinventario.model.OrdenCompra;
import dsw.msinventario.service.ComprasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ordenes-compra")
@RequiredArgsConstructor
public class OrdenCompraController {

    private final ComprasService comprasService;

    @GetMapping
    public ResponseEntity<List<OrdenCompra>> listar() {
        return ResponseEntity.ok(comprasService.listarOrdenes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenCompra> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(comprasService.obtenerOrden(id));
    }

    @PostMapping
    public ResponseEntity<OrdenCompra> crear(@RequestBody OrdenCompra oc) {
        return ResponseEntity.status(HttpStatus.CREATED).body(comprasService.crearOrden(oc));
    }

    /** Body: { "estado": "APROBADA" }  (PENDIENTE / APROBADA / RECHAZADA / RECIBIDA) */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<OrdenCompra> cambiarEstado(@PathVariable Long id,
                                                     @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(comprasService.cambiarEstadoOrden(id, body.get("estado")));
    }
}
