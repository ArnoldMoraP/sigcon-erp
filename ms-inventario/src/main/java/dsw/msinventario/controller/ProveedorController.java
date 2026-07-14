package dsw.msinventario.controller;

import dsw.msinventario.model.Proveedor;
import dsw.msinventario.service.ComprasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ComprasService comprasService;

    @GetMapping
    public ResponseEntity<List<Proveedor>> listar() {
        return ResponseEntity.ok(comprasService.listarProveedores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(comprasService.obtenerProveedor(id));
    }

    @PostMapping
    public ResponseEntity<Proveedor> crear(@RequestBody Proveedor p) {
        return ResponseEntity.status(HttpStatus.CREATED).body(comprasService.crearProveedor(p));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> actualizar(@PathVariable Long id, @RequestBody Proveedor p) {
        return ResponseEntity.ok(comprasService.actualizarProveedor(id, p));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        comprasService.eliminarProveedor(id);
        return ResponseEntity.noContent().build();
    }
}
