package dsw.msinventario.controller;

import dsw.msinventario.model.Compra;
import dsw.msinventario.service.ComprasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/compras")
@RequiredArgsConstructor
public class CompraController {

    private final ComprasService comprasService;

    @GetMapping
    public ResponseEntity<List<Compra>> listar() {
        return ResponseEntity.ok(comprasService.listarCompras());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Compra> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(comprasService.obtenerCompra(id));
    }

    /** Recibe la orden de compra APROBADA -> registra la compra y SUMA stock al inventario. */
    @PostMapping("/desde-orden/{ordenCompraId}")
    public ResponseEntity<Compra> registrarDesdeOrden(@PathVariable Long ordenCompraId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(comprasService.registrarDesdeOrden(ordenCompraId));
    }
}
