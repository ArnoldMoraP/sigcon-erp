package dsw.msauth.controller;

import dsw.msauth.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Endpoint INTERNO, para consumo entre microservicios (no para el frontend).
 *
 * Existe porque ms-ventas ya no puede hacer JOIN a seguridad.usuario:
 * cada microservicio tiene su propia base de datos. En vez de compartir la
 * tabla, ms-ventas pregunta por REST a ms-auth quien es el vendedor, una sola
 * vez al crear la cotizacion, y guarda el nombre en su propia tabla.
 *
 * Es el mismo patron que usa ms-ventas con ms-inventario (InventarioClient).
 */
@RestController
@RequestMapping("/interno/usuarios")
public class UsuarioInternoController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /** GET /interno/usuarios/{id}  ->  { "id": 1, "username": "admin" } */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtener(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(u -> ResponseEntity.ok(Map.<String, Object>of(
                        "id", u.getId(),
                        "username", u.getUsername())))
                .orElse(ResponseEntity.notFound().build());
    }
}