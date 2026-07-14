package dsw.msrrhh.controller;

import dsw.msrrhh.model.Empleado;
import dsw.msrrhh.service.EmpleadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Prefijo /rrhh/* para que el gateway necesite UNA sola ruta: /rrhh/** -> lb://ms-rrhh */
@RestController
@RequestMapping("/rrhh/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    @GetMapping
    public ResponseEntity<List<Empleado>> listar() {
        return ResponseEntity.ok(empleadoService.listar());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Empleado>> activos() {
        return ResponseEntity.ok(empleadoService.listarActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empleado> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(empleadoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Empleado> crear(@RequestBody Empleado e) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empleadoService.crear(e));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Empleado> actualizar(@PathVariable Long id, @RequestBody Empleado e) {
        return ResponseEntity.ok(empleadoService.actualizar(id, e));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Empleado> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(empleadoService.desactivar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        empleadoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
