package dsw.msventas.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Cliente REST hacia ms-auth, descubierto por Eureka (lb://ms-auth).
 *
 * Reemplaza el JOIN que CotizacionRepository hacia a seguridad.usuario.
 * Ahora que cada microservicio tiene su propia base de datos, ms-ventas
 * NO puede leer la tabla de ms-auth: se la pide por REST.
 *
 * Se llama UNA sola vez, al crear la cotizacion; el nombre resuelto se guarda
 * en comercial.cotizacion.vendedor (desnormalizacion), asi el listado no
 * necesita ninguna llamada adicional.
 */
@Component
@Slf4j
public class AuthClient {

    @Autowired
    private RestTemplate restTemplate;   // el @LoadBalanced que ya existe

    private static final String URL = "http://ms-auth/interno/usuarios/";

    /**
     * Devuelve el username del usuario, o null si ms-auth no responde.
     * No lanza excepcion: si el servicio esta caido, la cotizacion se crea
     * igual (solo queda sin nombre de vendedor). Degradacion controlada.
     */
    public String obtenerUsername(Long usuarioId) {
        if (usuarioId == null) return null;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> resp = restTemplate.getForObject(URL + usuarioId, Map.class);
            if (resp == null) return null;
            Object u = resp.get("username");
            return (u == null ? null : u.toString());
        } catch (Exception e) {
            log.warn("No se pudo resolver el vendedor (usuario_id={}) contra ms-auth: {}",
                    usuarioId, e.getMessage());
            return null;
        }
    }
}