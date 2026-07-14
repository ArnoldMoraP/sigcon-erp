package dsw.msauth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
        @org.springframework.lang.NonNull HttpServletRequest request,
        @org.springframework.lang.NonNull HttpServletResponse response,
        @org.springframework.lang.NonNull FilterChain chain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        if (!jwtUtil.isTokenValid(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String type = jwtUtil.getClaimFromToken(token, "type");
        if (!"access".equals(type)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String username = jwtUtil.getUsernameFromToken(token);
        String rol = jwtUtil.getClaimFromToken(token, "rol");

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            username, null,
            List.of(new SimpleGrantedAuthority("ROLE_" + rol))
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@org.springframework.lang.NonNull HttpServletRequest request) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String path = request.getServletPath();
        return path.equals("/login") || path.equals("/auth/refresh");
    }
}