package dsw.apigateway.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * FASE 2. Filtro global del api-gateway: valida el JWT ANTES de enrutar la
 * peticion a cualquier microservicio.
 *
 * Antes de esta clase, el gateway enrutaba sin validar nada: cualquiera podia
 * pegarle a /inventario, /rrhh, /pedidos, etc. sin token. Ahora:
 *
 *   - OPTIONS (preflight CORS)  -> pasa sin token.
 *   - /auth/login, /auth/refresh, /actuator/health  -> publicas, pasan sin token.
 *   - cualquier otra ruta       -> exige "Authorization: Bearer <access-token>"
 *                                  valido; si no, responde 401 y NO enruta.
 *
 * Modelo de confianza: los microservicios de aguas abajo (ms-ventas,
 * ms-inventario, ms-rrhh) no tienen seguridad propia, pero sus puertos ya no
 * estan publicados al host (FASE 2 en docker-compose), asi que la unica forma
 * de llegar a ellos es a traves de este gateway, que ya valido el token.
 * Ademas se les reenvia la identidad en cabeceras de confianza (X-Auth-*),
 * que el gateway SIEMPRE sobrescribe para que el cliente no pueda falsificarlas.
 */
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final ObjectMapper mapper = new ObjectMapper();

    /** Rutas que NO requieren token. */
    private static final List<String> PUBLICAS = List.of(
            "/auth/login",
            "/auth/refresh",
            "/actuator/health"
    );

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // Preflight CORS: el navegador manda OPTIONS sin token, hay que dejarlo pasar.
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        // Rutas publicas.
        if (esPublica(path)) {
            return chain.filter(exchange);
        }

        String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            return rechazar(exchange, "Falta el token de autenticacion (Authorization: Bearer ...)");
        }

        String token = header.substring(7);
        try {
            Claims claims = jwtUtil.validar(token);

            // Solo se aceptan access tokens para acceder a los recursos.
            if (!"access".equals(claims.get("type", String.class))) {
                return rechazar(exchange, "Se requiere un access token (no un refresh token)");
            }

            String usuario = claims.getSubject();
            String rol = claims.get("rol", String.class);

            // Reenviar la identidad ya validada a los microservicios de aguas abajo.
            // .header(...) SOBRESCRIBE cualquier X-Auth-* que el cliente intente inyectar.
            ServerHttpRequest mutada = request.mutate()
                    .header("X-Auth-User", usuario == null ? "" : usuario)
                    .header("X-Auth-Rol", rol == null ? "" : rol)
                    .build();

            return chain.filter(exchange.mutate().request(mutada).build());

        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token rechazado para {} {}: {}", request.getMethod(), path, e.getMessage());
            return rechazar(exchange, "Token invalido o expirado");
        }
    }

    private boolean esPublica(String path) {
        for (String p : PUBLICAS) {
            if (path.equals(p) || path.startsWith(p + "/")) {
                return true;
            }
        }
        return false;
    }

    private Mono<Void> rechazar(ServerWebExchange exchange, String mensaje) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        byte[] body;
        try {
            body = mapper.writeValueAsBytes(Map.of("error", mensaje, "status", 401));
        } catch (Exception e) {
            body = ("{\"error\":\"" + mensaje + "\",\"status\":401}").getBytes(StandardCharsets.UTF_8);
        }
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body)));
    }

    @Override
    public int getOrder() {
        // Ejecutar antes del filtro de enrutamiento de Spring Cloud Gateway.
        return -1;
    }
}
