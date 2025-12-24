package uz.codebyz.ads.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;
import java.util.UUID;

@Service
public class JwtTokenService {

    private final Key key;

    public JwtTokenService(@Value("${jwt.secret}") String secret) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("jwt.secret must be at least 32 characters");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public JwtUser parse(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        UUID userId = UUID.fromString(claims.getSubject());
        Object rolesClaim = claims.get("roles");
        String role;
        if (rolesClaim instanceof List<?> list && !list.isEmpty()) {
            role = list.get(0).toString();
        } else {
            role = claims.get("role", String.class);
        }
        return new JwtUser(userId, role);
    }
}
