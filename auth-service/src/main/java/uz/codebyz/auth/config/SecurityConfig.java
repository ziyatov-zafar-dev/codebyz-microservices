package uz.codebyz.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import uz.codebyz.auth.security.JwtAuthFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthFilter jwtAuthFilter) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults()) // 🔥 MANA SHU YETISHMAYOTGANDI
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        http.authorizeHttpRequests(reg -> reg
                .requestMatchers(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/api/auth/sign-in",
                        "/api/auth/sign-in/verify",
                        "/api/auth/sign-up",
                        "/odnlicasjocdiahduhjcoinaurofrejdhiudosjkhfddddddddddddddddddddiopasdijkhieodfjhsiui0eodjifhureodihuosfdjfiles/**", "/uploads/**",
                        "/api/auth/sign-up/verify",
                        "/api/auth/reset-password",
                        "/api/auth/forgot-password",
                        "/api/auth/logout",
                        "/api/users/exists/**"
                ).permitAll()
                .requestMatchers("/api/users/**").hasAnyRole("TEACHER", "ADMIN", "STUDENT")
                .anyRequest().authenticated()
        );

        http.oauth2Login(Customizer.withDefaults());
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
