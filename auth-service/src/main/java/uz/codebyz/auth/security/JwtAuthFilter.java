package uz.codebyz.auth.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uz.codebyz.auth.device.UserDeviceRepository;
import uz.codebyz.auth.session.RevokedAccessTokenRepository;
import uz.codebyz.auth.user.UserRepository;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenService tokenService;
    private final UserRepository userRepo;
    private final UserDeviceRepository userDeviceRepository;

    public JwtAuthFilter(JwtTokenService tokenService,
                         RevokedAccessTokenRepository revokedAccessTokenRepository, UserRepository userRepo, UserDeviceRepository userDeviceRepository) {
        this.tokenService = tokenService;
        this.userRepo = userRepo;
        this.userDeviceRepository = userDeviceRepository;
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
            JwtUser jwtUser;
            try {
                jwtUser = tokenService.parseAccessToken(token);
            } catch (Exception e) {
                System.err.println("error parsing token: " + e.getMessage());
                return;
            }

            // 🔥 TOKEN VERSION TEKSHIRUV
            int tokenVersionFromJwt = jwtUser.getTokenVersion();
            int currentTokenVersion = userRepo.findTokenVersionById(jwtUser.getUserId());

            if (tokenVersionFromJwt != currentTokenVersion) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String deviceId = jwtUser.getDeviceId();
            if (deviceId == null || deviceId.isBlank()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            boolean deviceActive =
                    userDeviceRepository.existsByUserIdAndDeviceIdAndActiveTrue(
                            jwtUser.getUserId(),
                            deviceId
                    );
            if (!deviceActive) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            jwtUser,
                            null,
                            jwtUser.getAuthorities()
                    );

            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception e) {
            System.err.println(e.getMessage());
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

}
