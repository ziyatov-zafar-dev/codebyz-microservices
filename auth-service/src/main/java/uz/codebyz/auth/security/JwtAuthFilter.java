package uz.codebyz.auth.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.http.HttpHeaders;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import uz.codebyz.auth.session.RevokedAccessTokenRepository;
//
//import java.io.IOException;
//import java.util.List;
//@Component
//public class JwtAuthFilter extends OncePerRequestFilter {
//
//    private final JwtTokenService tokenService;
//    private final RevokedAccessTokenRepository revokedAccessTokenRepository;
//
//    public JwtAuthFilter(JwtTokenService tokenService, RevokedAccessTokenRepository revokedAccessTokenRepository) {
//        this.tokenService = tokenService;
//        this.revokedAccessTokenRepository = revokedAccessTokenRepository;
//    }
//
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain
//    ) throws ServletException, IOException {
//
//        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
//
//        if (header != null && header.startsWith("Bearer ")) {
//            try {
//
//                JwtUser jwtUser = tokenService.parseAccessToken(header.substring(7));
//
//                UsernamePasswordAuthenticationToken auth =
//                        new UsernamePasswordAuthenticationToken(
//                                jwtUser,
//                                null,
//                                jwtUser.getAuthorities()
//                        );
//
//                SecurityContextHolder.getContext().setAuthentication(auth);
//
//            } catch (Exception e) {
//                SecurityContextHolder.clearContext();
//            }
//        }
//
//
//        filterChain.doFilter(request, response);
//    }
//}


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uz.codebyz.auth.session.RevokedAccessTokenRepository;
import uz.codebyz.auth.user.UserRepository;

import java.io.IOException;
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenService tokenService;
    private final RevokedAccessTokenRepository revokedAccessTokenRepository;
    private final UserRepository userRepo;

    public JwtAuthFilter(JwtTokenService tokenService,
                         RevokedAccessTokenRepository revokedAccessTokenRepository, UserRepository userRepo) {
        this.tokenService = tokenService;
        this.revokedAccessTokenRepository = revokedAccessTokenRepository;
        this.userRepo = userRepo;
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

            // 🔥 TOKEN VERSION TEKSHIRUV
            int tokenVersionFromJwt = jwtUser.getTokenVersion();
            int currentTokenVersion = userRepo.findTokenVersionById(jwtUser.getUserId());

            if (tokenVersionFromJwt != currentTokenVersion) {
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
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

}
