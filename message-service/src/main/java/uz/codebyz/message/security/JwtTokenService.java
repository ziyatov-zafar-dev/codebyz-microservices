package uz.codebyz.message.security;

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
        byte[] raw = secret.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(raw.length >= 32 ? raw : new byte[32]);
    }

    public JwtUser parse(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        UUID userId = UUID.fromString(claims.getSubject());
        String role = ((List<?>) claims.get("roles")).get(0).toString();

        return new JwtUser(userId, role);
    }
}
