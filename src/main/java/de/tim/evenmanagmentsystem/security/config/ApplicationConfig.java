package de.tim.evenmanagmentsystem.security.config;

import de.tim.evenmanagmentsystem.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Konfigurationsklasse für Authentifizierungskomponenten.
 * Definiert Beans für UserDetailsService, AuthenticationProvider, AuthenticationManager und PasswordEncoder.
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    /**
     * Konfiguriert den UserDetailsService, der Benutzer aus der Datenbank lädt.
     * In Spring Security wird die E-Mail-Adresse als "username" behandelt.
     *
     * @return Ein UserDetailsService, der Benutzer anhand ihrer E-Mail-Adresse lädt
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    /**
     * Konfiguriert den AuthenticationProvider, der für die Authentifizierung zuständig ist.
     * Verwendet DaoAuthenticationProvider, der Benutzer aus der Datenbank lädt und Passwörter überprüft.
     *
     * @return Ein konfigurierter AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        // Konfiguriere den UserDetailsService für das Laden von Benutzern
        authProvider.setUserDetailsService(userDetailsService());

        // Konfiguriere den PasswordEncoder für die Passwortüberprüfung
        authProvider.setPasswordEncoder(passwordEncoder());

        // Aktiviere die Anzeige von detaillierten Fehlermeldungen (nur für Entwicklung)
        // In der Produktion sollte dies deaktiviert werden
        authProvider.setHideUserNotFoundExceptions(false);

        return authProvider;
    }

    /**
     * Konfiguriert den AuthenticationManager, der den Authentifizierungsprozess steuert.
     *
     * @param config Die AuthenticationConfiguration von Spring Security
     * @return Ein konfigurierter AuthenticationManager
     * @throws Exception Wenn die Konfiguration fehlschlägt
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Konfiguriert den PasswordEncoder, der Passwörter hasht und verifiziert.
     * Verwendet BCryptPasswordEncoder mit Standardeinstellungen (10 Runden).
     *
     * @return Ein konfigurierter PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}