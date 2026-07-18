package dsw.msauth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expiration;
    private final long refreshExpiration;

    public JwtUtil(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.expiration}") long expiration,
        @Value("${jwt.refresh-expiration}") long refreshExpiration
    ) {
        // FASE 3: antes se hacia Base64.getEncoder().encode(...) (CODIFICAR),
        // lo cual era consistente consigo mismo pero no con la forma estandar.
        // Ahora la clave se deriva de los bytes UTF-8 del secreto, IGUAL que en
        // el api-gateway (JwtUtil), para que el gateway pueda validar estos tokens.
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
        this.refreshExpiration = refreshExpiration;
    }

    public String generateAccessToken(String username, String rol) {
        return Jwts.builder()
            .subject(username)
            .claim("rol", rol)
            .claim("type", "access")
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(key)
            .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
            .subject(username)
            .claim("type", "refresh")
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
            .signWith(key)
            .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public boolean isTokenValid(String token) {
        try {
            validateToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return validateToken(token).getSubject();
    }

    public String getClaimFromToken(String token, String claim) {
        return validateToken(token).get(claim, String.class);
    }
}