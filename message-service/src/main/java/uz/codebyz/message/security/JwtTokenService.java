package uz.codebyz.message.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtTokenService {

    private final SecretKey secretKey;

    public JwtTokenService(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Authentication parse(String token) {
        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);

        Claims claims = claimsJws.getBody();
        UUID userId = UUID.fromString(claims.getSubject());
        String role = extractRole(claims);
        List<String> roles = List.of(role);
        JwtUser principal = new JwtUser(userId, roles);
        return new UsernamePasswordAuthenticationToken(principal, null,
                roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
    }

    private String extractRole(Claims claims) {
        Object rolesObj = claims.get("roles");
        String rawRole = null;
        if (rolesObj instanceof List<?> list && !list.isEmpty()) {
            rawRole = String.valueOf(list.get(0));
        } else if (rolesObj instanceof String str && !str.isBlank()) {
            rawRole = str;
        }
        if (rawRole == null || rawRole.isBlank()) {
            throw new IllegalArgumentException("Role topilmadi");
        }
        String roleUpper = rawRole.trim().toUpperCase();
        if (!ALLOWED_ROLES.contains(roleUpper)) {
            throw new IllegalArgumentException("Noto'g'ri role: " + roleUpper);
        }
        return roleUpper;
    }

    private static final Set<String> ALLOWED_ROLES = Set.of("ADMIN", "TEACHER", "STUDENT");
}
