package uz.codebyz.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import uz.codebyz.auth.config.properties.JwtProperties;
import uz.codebyz.auth.user.User;
import uz.codebyz.auth.user.UserRole;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtTokenService {

    private final JwtProperties props;
    private final Key key;

    public JwtTokenService(JwtProperties props) {
        this.props = props;

        byte[] raw = props.getSecret().getBytes(StandardCharsets.UTF_8);
        if (raw.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(raw, 0, padded, 0, raw.length);
            raw = padded;
        }
        this.key = Keys.hmacShaKeyFor(raw);
    }

    // =========================
    // 🔹 CLAIMS PARSE
    // =========================
    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // =========================
    // 🔹 ACCESS TOKEN (FAOL)
    // =========================
    public String createAccessToken(User user, String jti) {
        Instant now = Instant.now();
        Instant exp = now.plus(props.getAccessTokenMinutes(), ChronoUnit.MINUTES);

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setId(jti) // 🔥 JTI
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .claim("username", user.getUsername())
                .claim("role", user.getRole().name())
                .claim("tv", user.getTokenVersion()) // 🔥 TOKEN VERSION
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ❌ BU METODNI O‘CHIRISH SHART
    // createAccessToken(User user) — ISHLATILMAYDI

    // =========================
    // 🔹 REFRESH TOKEN
    // =========================
    public String createRefreshToken(User user, String jti) {
        Instant now = Instant.now();
        Instant exp = now.plus(props.getRefreshTokenDays(), ChronoUnit.DAYS);

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setId(jti)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .claim("type", "refresh")
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // =========================
    // 🔹 ACCESS TOKEN PARSE
    // =========================
    public JwtUser parseAccessToken(String token) {
        Claims claims = parseClaims(token);

        UUID userId = UUID.fromString(claims.getSubject());
        String username = claims.get("username", String.class);
        String roleStr = claims.get("role", String.class);
        Integer tokenVersion = claims.get("tv", Integer.class);

        if (tokenVersion == null) {
            throw new RuntimeException("Token version missing");
        }

        UserRole role = roleStr == null
                ? UserRole.STUDENT
                : UserRole.valueOf(roleStr);

        return new JwtUser(userId, username, role, tokenVersion);
    }
}
