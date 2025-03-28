package de.tim.evenmanagmentsystem.security.config;

import de.tim.evenmanagmentsystem.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Konfigurationsklasse für Spring Security.
 * Definiert Sicherheitsrichtlinien, Filter und geschützte Endpunkte.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final LogoutHandler logoutHandler;

    /**
     * Konfiguriert die Security-Filter-Chain.
     * Definiert Sicherheitsrichtlinien und geschützte Endpunkte.
     *
     * @param http Das HttpSecurity-Objekt, das konfiguriert werden soll
     * @return Die konfigurierte SecurityFilterChain
     * @throws Exception Wenn die Konfiguration fehlschlägt
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deaktiviere CSRF, da wir zustandslose JWT-Authentifizierung verwenden
                .csrf(AbstractHttpConfigurer::disable)

                // Konfiguriere CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Konfiguriere Exception-Handling
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthEntryPoint)
                )

                // Konfiguriere Session-Management (zustandslos)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Konfiguriere URL-basierte Autorisierung
                .authorizeHttpRequests(auth -> auth
                        // Öffentliche Endpunkte
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/events/*/details").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Event
                        .requestMatchers(HttpMethod.GET,"/api/events").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/events").hasRole("ORGANIZER")
                        .requestMatchers(HttpMethod.PATCH, "/api/events/**").hasRole("ORGANIZER")
                        .requestMatchers(HttpMethod.DELETE, "/api/events/**").hasRole("ORGANIZER")

                        // User
                        .requestMatchers("/api/attendees/**").hasRole("ATTENDEE")
                        .requestMatchers("/api/organizers/**").hasRole("ORGANIZER")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Alle anderen Anfragen erfordern Authentifizierung
                        .anyRequest().authenticated()
                )

                // Konfiguriere Logout
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler((request, response, authentication) ->
                                SecurityContextHolder.clearContext()
                        )
                )

                // Konfiguriere Authentication Provider
                .authenticationProvider(authenticationProvider)

                // Füge JWT-Filter vor dem UsernamePasswordAuthenticationFilter ein
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Konfiguriert CORS (Cross-Origin Resource Sharing).
     * Ermöglicht den Zugriff von anderen Domains auf unsere API.
     *
     * @return Ein konfigurierter CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000", // Lokale Entwicklung
                "https://youreventapp.com" // Produktions-Frontend
        ));

        // Erlaubte HTTP-Methoden
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Erlaubte HTTP-Header
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        // Exponierte Header (die dem Client zugänglich gemacht werden)
        configuration.setExposedHeaders(List.of("Authorization"));

        // Erlaube Cookies in Cross-Origin-Anfragen
        configuration.setAllowCredentials(true);

        // Wie lange das Preflight-Ergebnis gecacht werden soll (in Sekunden)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}