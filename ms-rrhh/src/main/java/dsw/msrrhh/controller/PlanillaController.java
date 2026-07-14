package dsw.msrrhh.controller;

import dsw.msrrhh.model.PagoPlanilla;
import dsw.msrrhh.model.Planilla;
import dsw.msrrhh.service.PlanillaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rrhh/planillas")
@RequiredArgsConstructor
public class PlanillaController {

    private final PlanillaService planillaService;

    @GetMapping
    public ResponseEntity<List<Planilla>> listar() {
        return ResponseEntity.ok(planillaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(planillaService.obtenerConDetalle(id));
    }

    /** Body: { "periodo": "2026-07", "usuario_id": 1 } */
    @PostMapping("/generar")
    public ResponseEntity<Map<String, Object>> generar(@RequestBody Map<String, Object> body) {
        String periodo = (String) body.get("periodo");
        Long usuarioId = body.get("usuario_id") == null
                ? null : Long.valueOf(body.get("usuario_id").toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(planillaService.generar(periodo, usuarioId));
    }

    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<Planilla> aprobar(@PathVariable Long id) {
        return ResponseEntity.ok(planillaService.aprobar(id));
    }

    /** Body opcional: { "metodo_pago": "TRANSFERENCIA" } */
    @PostMapping("/{id}/pagar")
    public ResponseEntity<PagoPlanilla> pagar(@PathVariable Long id,
                                              @RequestBody(required = false) Map<String, String> body) {
        String metodo = (body == null ? null : body.get("metodo_pago"));
        return ResponseEntity.status(HttpStatus.CREATED).body(planillaService.pagar(id, metodo));
    }

    @GetMapping("/pagos")
    public ResponseEntity<List<PagoPlanilla>> pagos() {
        return ResponseEntity.ok(planillaService.listarPagos());
    }
}
