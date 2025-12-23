package uz.codebyz.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import uz.codebyz.auth.config.properties.JwtProperties;
import uz.codebyz.auth.dto.AuthTokensResponse;
import uz.codebyz.auth.session.RefreshToken;
import uz.codebyz.auth.session.RefreshTokenRepository;
import uz.codebyz.auth.user.User;
import uz.codebyz.auth.user.UserRole;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class JwtTokenService {

    private final JwtProperties props;
    private final Key key;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtTokenService(JwtProperties props, RefreshTokenRepository refreshTokenRepository) {
        this.props = props;

        byte[] raw = props.getSecret().getBytes(StandardCharsets.UTF_8);
        if (raw.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(raw, 0, padded, 0, raw.length);
            raw = padded;
        }
        this.key = Keys.hmacShaKeyFor(raw);
        this.refreshTokenRepository = refreshTokenRepository;
    }

    // =========================
    // 🔹 CLAIMS PARSE
    // =========================
    public Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    // =========================
    // 🔹 ACCESS TOKEN (FAOL)
    // =========================
    public String createAccessToken(User user, String deviceId, String jti) {
        Instant now = Instant.now();
        Instant exp = now.plus(props.getAccessTokenMinutes(), ChronoUnit.MINUTES);

        return Jwts.builder().setSubject(user.getId().toString())          // userId
                .setId(jti)                                   // jti
                .setIssuedAt(Date.from(now)).setExpiration(Date.from(exp))

                // ===== CUSTOM CLAIMS =====
                .claim("username", user.getUsername())
                .claim("roles", List.of(user.getRole().name())) // 🔥 LIST
                .claim("tokenVersion", user.getTokenVersion())  // 🔥 readable
                .claim("deviceId", deviceId)                    // 🔥 device session

                .signWith(key, SignatureAlgorithm.HS256).compact();
    }


    // ❌ BU METODNI O‘CHIRISH SHART
    // createAccessToken(User user) — ISHLATILMAYDI

    // =========================
    // 🔹 REFRESH TOKEN
    // =========================
    public String createRefreshToken(User user, String jti) {
        Instant now = Instant.now();
        Instant exp = now.plus(props.getRefreshTokenDays(), ChronoUnit.DAYS);

        return Jwts.builder().setSubject(user.getId().toString()).setId(jti).setIssuedAt(Date.from(now)).setExpiration(Date.from(exp)).claim("type", "refresh").signWith(key, SignatureAlgorithm.HS256).compact();
    }

    // =========================
    // 🔹 ACCESS TOKEN PARSE
    // =========================
    public JwtUser parseAccessToken(String token) {

        Claims claims = parseClaims(token);

        UUID userId = UUID.fromString(claims.getSubject());
        String username = claims.get("username", String.class);
        String roleStr = claims.get("roles", List.class).get(0).toString();
        Integer tokenVersion = claims.get("tokenVersion", Integer.class);
        String deviceId = claims.get("deviceId", String.class);

//        if (tokenVersion==null)tokenVersion=0;
        if (tokenVersion == null) {
            throw new RuntimeException("Token version missing");
        }

        UserRole role = roleStr == null ? UserRole.STUDENT : UserRole.valueOf(roleStr);

        return new JwtUser(userId, username, role, tokenVersion, deviceId);
    }

    public AuthTokensResponse generateTokens(User user, String deviceId) {

        // 1️⃣ JTI yaratamiz
        String jti = UUID.randomUUID().toString();

        // 2️⃣ TOKENLARNI YARATAMIZ
        String accessToken = createAccessToken(user, deviceId,jti);
        String refreshToken = createRefreshToken(user, jti);

        // 3️⃣ REFRESH TOKENNI DB’GA SAQLAYMIZ
        RefreshToken rt = new RefreshToken();
        rt.setUserId(user.getId());
        rt.setDeviceId(deviceId);
        rt.setJti(jti);
        rt.setRevoked(false);
        rt.setExpiresAt(Instant.now().plus(props.getRefreshTokenDays(), ChronoUnit.DAYS));

        refreshTokenRepository.save(rt);

        // 4️⃣ FRONTEND’GA QAYTARAMIZ
        return new AuthTokensResponse(accessToken, refreshToken);
    }

}
