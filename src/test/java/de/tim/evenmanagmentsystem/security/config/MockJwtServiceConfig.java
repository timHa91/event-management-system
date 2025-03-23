package de.tim.evenmanagmentsystem.security.config;

import de.tim.evenmanagmentsystem.security.repository.TokenRepository;
import de.tim.evenmanagmentsystem.security.service.JwtService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;

/**
 * Testkonfiguration, die Mocks für JwtService und TokenRepository bereitstellt.
 * Diese Beans werden für die Integration Tests benötigt.
 */
@TestConfiguration
@Profile("test")
public class MockJwtServiceConfig {

  /**
   * Mock für JwtService.
   */
  @MockBean
  private JwtService jwtService;

  /**
   * Mock für TokenRepository.
   */
  @MockBean
  private TokenRepository tokenRepository;
}