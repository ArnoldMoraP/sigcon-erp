package dsw.msinventario.controller;

import dsw.msinventario.model.Presupuesto;
import dsw.msinventario.service.ComprasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/presupuestos")
@RequiredArgsConstructor
public class PresupuestoController {

    private final ComprasService comprasService;

    @GetMapping
    public ResponseEntity<List<Presupuesto>> listar() {
        return ResponseEntity.ok(comprasService.listarPresupuestos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Presupuesto> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(comprasService.obtenerPresupuesto(id));
    }

    @PostMapping
    public ResponseEntity<Presupuesto> crear(@RequestBody Presupuesto p) {
        return ResponseEntity.status(HttpStatus.CREATED).body(comprasService.crearPresupuesto(p));
    }

    /** Body: { "monto": 500.00 } */
    @PatchMapping("/{id}/ejecutar")
    public ResponseEntity<Presupuesto> ejecutar(@PathVariable Long id,
                                                @RequestBody Map<String, BigDecimal> body) {
        return ResponseEntity.ok(comprasService.ejecutarPresupuesto(id, body.get("monto")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        comprasService.eliminarPresupuesto(id);
        return ResponseEntity.noContent().build();
    }
}
