package dsw.msrrhh.controller;

import dsw.msrrhh.model.IncidenciaPersonal;
import dsw.msrrhh.service.IncidenciaPersonalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rrhh/incidencias")
@RequiredArgsConstructor
public class IncidenciaPersonalController {

    private final IncidenciaPersonalService incidenciaService;

    @GetMapping
    public ResponseEntity<List<IncidenciaPersonal>> listar() {
        return ResponseEntity.ok(incidenciaService.listar());
    }

    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<IncidenciaPersonal>> porEmpleado(@PathVariable Long empleadoId) {
        return ResponseEntity.ok(incidenciaService.listarPorEmpleado(empleadoId));
    }

    @PostMapping
    public ResponseEntity<IncidenciaPersonal> crear(@RequestBody IncidenciaPersonal i) {
        return ResponseEntity.status(HttpStatus.CREATED).body(incidenciaService.crear(i));
    }

    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<IncidenciaPersonal> cerrar(@PathVariable Long id) {
        return ResponseEntity.ok(incidenciaService.cerrar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        incidenciaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
