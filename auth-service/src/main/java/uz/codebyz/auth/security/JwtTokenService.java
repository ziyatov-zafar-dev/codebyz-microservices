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
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class JwtTokenService {

    private final JwtProperties props;
    private final Key key;
    private final RefreshTokenRepository refreshTokenRepository;
    private final Clock clock;

    public JwtTokenService(JwtProperties props, RefreshTokenRepository refreshTokenRepository, Clock clock) {
        this.props = props;
        this.clock = clock;

        byte[] raw = props.getSecret().getBytes(StandardCharsets.UTF_8);
        if (raw.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(raw, 0, padded, 0, raw.length);
            raw = padded;
        }
        this.key = Keys.hmacShaKeyFor(raw);
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String createAccessToken(User user, String deviceId, String jti) {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime exp = now.plus(props.getAccessTokenMinutes(), ChronoUnit.MINUTES);

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setId(jti)
                .setIssuedAt(Date.from(now.atZone(clock.getZone()).toInstant()))
                .setExpiration(Date.from(exp.atZone(clock.getZone()).toInstant()))
                .claim("username", user.getUsername())
                .claim("roles", List.of(user.getRole().name()))
                .claim("tokenVersion", user.getTokenVersion())
                .claim("deviceId", deviceId)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(User user, String jti) {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime exp = now.plus(props.getRefreshTokenDays(), ChronoUnit.DAYS);
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setId(jti)
                .setIssuedAt(Date.from(now.atZone(clock.getZone()).toInstant()))
                .setExpiration(Date.from(exp.atZone(clock.getZone()).toInstant()))
                .claim("type", "refresh")
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public JwtUser parseAccessToken(String token) {
        Claims claims = parseClaims(token);
        UUID userId = UUID.fromString(claims.getSubject());
        String username = claims.get("username", String.class);
        String roleStr = claims.get("roles", List.class).get(0).toString();
        Integer tokenVersion = claims.get("tokenVersion", Integer.class);
        String deviceId = claims.get("deviceId", String.class);
        if (tokenVersion == null) {
            throw new RuntimeException("Token version missing");
        }
        UserRole role = roleStr == null ? UserRole.STUDENT : UserRole.valueOf(roleStr);
        return new JwtUser(userId, username, role, tokenVersion, deviceId);
    }

    public AuthTokensResponse generateTokens(User user, String deviceId) {
        String jti = UUID.randomUUID().toString();
        String accessToken = createAccessToken(user, deviceId, jti);
        String refreshToken = createRefreshToken(user, jti);

        RefreshToken rt = new RefreshToken();
        rt.setUserId(user.getId());
        rt.setDeviceId(deviceId);
        rt.setJti(jti);
        rt.setRevoked(false);
        LocalDateTime now = LocalDateTime.now(clock);
        rt.setCreatedAt(now);
        rt.setExpiresAt(now.plus(props.getRefreshTokenDays(), ChronoUnit.DAYS));
        refreshTokenRepository.save(rt);

        return new AuthTokensResponse(accessToken, refreshToken);
    }
}
