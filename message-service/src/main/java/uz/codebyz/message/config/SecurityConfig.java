package uz.codebyz.message.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import uz.codebyz.message.security.JwtAuthFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtFilter;

    public SecurityConfig(JwtAuthFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults()) // 🔥 MANA SHU YETISHMAYOTGANDI
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/odnlicasjocdiahduhjcoinaurofrejdhiudosjkhfddddddddddddddddddddiopasdijkhieodfjhsiui0eodjifhureodihuosfdjfiles/**",
                                "/v3/api-docs"
                        ).permitAll()
                        .requestMatchers("/api/messages/**", "/api/chats/**")
                        .hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                        .anyRequest()
                        .hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                )
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
