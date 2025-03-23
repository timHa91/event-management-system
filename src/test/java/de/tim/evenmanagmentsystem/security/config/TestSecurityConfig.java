package de.tim.evenmanagmentsystem.security.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Testkonfiguration für Spring Security, die für Integrationstests verwendet
 * wird.
 * Diese Konfiguration überschreibt die Standardsicherheitskonfiguration für
 * Tests.
 */
@TestConfiguration
@EnableWebSecurity
@Profile("test")
public class TestSecurityConfig {

    /**
     * Konfiguriert einen vereinfachten SecurityFilterChain für Tests.
     * Erlaubt Zugriff auf öffentliche Endpunkte und erfordert Authentifizierung für
     * andere.
     */
    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/api/events", "/api/events/*/details").permitAll()
                        .requestMatchers("/api/attendees/**").hasRole("ATTENDEE")
                        .requestMatchers("/api/organizers/**").hasRole("ORGANIZER")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated());

        return http.build();
    }

    /**
     * Konfiguriert einen In-Memory UserDetailsService für Tests.
     * Erstellt einen Testbenutzer mit Benutzername "test@example.com" und Passwort
     * "password".
     */
    @Bean
    public UserDetailsService testUserDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withUsername("test@example.com")
                        .password("{noop}password")
                        .roles("USER")
                        .build());
    }
}