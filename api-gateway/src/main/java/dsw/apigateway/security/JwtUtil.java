package dsw.apigateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * FASE 2. Validacion de JWT en el api-gateway.
 *
 * La clave HMAC se deriva EXACTAMENTE igual que en ms-auth (FASE 3):
 * a partir de los bytes UTF-8 del secreto compartido (jwt.secret == JWT_SECRET).
 * Si ms-auth y el gateway derivaran la clave distinto, la firma no validaria
 * y TODOS los tokens serian rechazados. Por eso ambos usan la misma linea.
 */
@Component
public class JwtUtil {

    private final SecretKey key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** Devuelve los claims si la firma y la expiracion son validas; si no, lanza JwtException. */
    public Claims validar(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
