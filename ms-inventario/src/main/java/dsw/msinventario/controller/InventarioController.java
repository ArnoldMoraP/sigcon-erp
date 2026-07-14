package dsw.msinventario.controller;

import dsw.msinventario.dto.AjusteStockRequest;
import dsw.msinventario.dto.DescontarStockRequest;
import dsw.msinventario.dto.DescontarStockResponse;
import dsw.msinventario.model.Inventario;
import dsw.msinventario.service.InventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @GetMapping
    public ResponseEntity<List<Inventario>> listar() {
        return ResponseEntity.ok(inventarioService.listar());
    }

    @GetMapping("/bajo-stock")
    public ResponseEntity<List<Inventario>> bajoStock() {
        return ResponseEntity.ok(inventarioService.listarBajoStock());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventario> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Inventario> crear(@RequestBody Inventario inv) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.crear(inv));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inventario> actualizar(@PathVariable Long id, @RequestBody Inventario inv) {
        return ResponseEntity.ok(inventarioService.actualizar(id, inv));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        inventarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /** Reposicion manual (entrada de stock). */
    @PatchMapping("/{id}/reponer")
    public ResponseEntity<Inventario> reponer(@PathVariable Long id, @RequestBody AjusteStockRequest req) {
        return ResponseEntity.ok(inventarioService.reponerStock(id, req.getCantidad()));
    }

    /**
     * ===== ENDPOINT DEL CONTRATO CON ms-ventas =====
     * Lo consume InventarioClient (ms-ventas) al aprobar un pedido.
     * NO cambiar ruta, verbo ni nombres de campos sin coordinar.
     *
     * 200 -> { "producto": "...", "stockNuevo": 195, "bajoStock": false }
     * 400 -> { "error": "Stock insuficiente. Stock actual: 3" }
     * 400 -> { "error": "Producto no encontrado en inventario" }
     */
    @PatchMapping("/descontar-stock")
    public ResponseEntity<DescontarStockResponse> descontarStock(@RequestBody DescontarStockRequest req) {
        return ResponseEntity.ok(inventarioService.descontarStock(req));
    }
}
