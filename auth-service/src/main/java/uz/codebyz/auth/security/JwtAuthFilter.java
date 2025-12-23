package uz.codebyz.auth.security;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uz.codebyz.auth.device.UserDeviceRepository;
import uz.codebyz.auth.security.JwtTokenService;
import uz.codebyz.auth.user.UserRepository;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenService tokenService;
    private final UserRepository userRepo;
    private final UserDeviceRepository userDeviceRepo;

    public JwtAuthFilter(
            JwtTokenService tokenService,
            UserRepository userRepo,
            UserDeviceRepository userDeviceRepo
    ) {
        this.tokenService = tokenService;
        this.userRepo = userRepo;
        this.userDeviceRepo = userDeviceRepo;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            JwtUser jwtUser = tokenService.parseAccessToken(token);

            // ===============================
            // 1️⃣ TOKEN VERSION CHECK
            // ===============================
            int tokenVersionFromJwt = jwtUser.getTokenVersion();
            int currentTokenVersion =
                    userRepo.findTokenVersionById(jwtUser.getUserId());
            if (tokenVersionFromJwt != currentTokenVersion) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // ===============================
            // 2️⃣ DEVICE ACTIVE CHECK 🔥
            // ===============================
            String deviceId = request.getHeader("X-Device-Id");

            if (deviceId == null || deviceId.isBlank()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            boolean deviceActive =
                    userDeviceRepo.existsByUserIdAndDeviceIdAndActiveTrue(
                            jwtUser.getUserId(),
                            deviceId
                    );

            if (!deviceActive) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // ===============================
            // 3️⃣ AUTH CONTEXT SET
            // ===============================
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            jwtUser,
                            null,
                            jwtUser.getAuthorities()
                    );

            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
