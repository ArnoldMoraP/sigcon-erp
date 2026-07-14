package dsw.msrrhh.controller;

import dsw.msrrhh.model.Asistencia;
import dsw.msrrhh.service.AsistenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rrhh/asistencias")
@RequiredArgsConstructor
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    @GetMapping
    public ResponseEntity<List<Asistencia>> listar() {
        return ResponseEntity.ok(asistenciaService.listar());
    }

    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<Asistencia>> porEmpleado(@PathVariable Long empleadoId) {
        return ResponseEntity.ok(asistenciaService.listarPorEmpleado(empleadoId));
    }

    /** Body: { "empleado_id": 1, "fecha": "2026-07-10", "hora_entrada": "08:25:00" } */
    @PostMapping
    public ResponseEntity<Asistencia> registrar(@RequestBody Asistencia a) {
        return ResponseEntity.status(HttpStatus.CREATED).body(asistenciaService.registrar(a));
    }

    /** Body opcional: { "hora_salida": "17:00:00" } */
    @PatchMapping("/{id}/salida")
    public ResponseEntity<Asistencia> marcarSalida(@PathVariable Long id,
                                                   @RequestBody(required = false) Map<String, String> body) {
        LocalTime hora = (body != null && body.get("hora_salida") != null)
                ? LocalTime.parse(body.get("hora_salida")) : null;
        return ResponseEntity.ok(asistenciaService.marcarSalida(id, hora));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        asistenciaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
