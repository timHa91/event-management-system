package de.tim.evenmanagmentsystem.security.integration;

import de.tim.evenmanagmentsystem.common.model.Address;
import de.tim.evenmanagmentsystem.security.model.Token;
import de.tim.evenmanagmentsystem.security.model.TokenType;
import de.tim.evenmanagmentsystem.security.repository.TokenRepository;
import de.tim.evenmanagmentsystem.security.service.JwtService;
import de.tim.evenmanagmentsystem.user.model.Attendee;
import de.tim.evenmanagmentsystem.user.model.Organizer;
import de.tim.evenmanagmentsystem.user.model.UserRole;
import de.tim.evenmanagmentsystem.user.model.UserStatus;
import de.tim.evenmanagmentsystem.user.repository.AttendeeRepository;
import de.tim.evenmanagmentsystem.user.repository.OrganizerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Configuration
@Profile("test")
public class TestDataInitializer {

    @Bean
    public CommandLineRunner initTestData(
            AttendeeRepository attendeeRepository,
            OrganizerRepository organizerRepository,
            TokenRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        return args -> {
            // Erstelle Testbenutzer
            Attendee attendee = createTestAttendee(passwordEncoder);
            attendee = attendeeRepository.save(attendee);

            Organizer organizer = createTestOrganizer(passwordEncoder);
            organizer = organizerRepository.save(organizer);

            // Erstelle Tokens für Testbenutzer
            createTokenForUser(attendee, "valid_attendee_token", false, false, tokenRepository);
            createTokenForUser(attendee, "expired_attendee_token", true, false, tokenRepository);
            createTokenForUser(organizer, "valid_organizer_token", false, false, tokenRepository);

            // Erstelle echte JWTs für Tests
            UserDetails attendeeDetails = User.builder()
                    .username(attendee.getEmail())
                    .password(attendee.getPassword())
                    .authorities("ROLE_ATTENDEE")
                    .build();

            UserDetails organizerDetails = User.builder()
                    .username(organizer.getEmail())
                    .password(organizer.getPassword())
                    .authorities("ROLE_ORGANIZER")
                    .build();

            String attendeeJwt = jwtService.generateToken(attendeeDetails);
            String organizerJwt = jwtService.generateToken(organizerDetails);

            createTokenForUser(attendee, attendeeJwt, false, false, tokenRepository);
            createTokenForUser(organizer, organizerJwt, false, false, tokenRepository);

            System.out.println("Test data initialized");
            System.out.println("Attendee JWT: " + attendeeJwt);
            System.out.println("Organizer JWT: " + organizerJwt);
        };
    }

    private Attendee createTestAttendee(PasswordEncoder passwordEncoder) {
        Attendee attendee = new Attendee();
        attendee.setEmail("test.attendee@example.com");
        attendee.setPassword(passwordEncoder.encode("password"));
        attendee.setFirstName("Test");
        attendee.setLastName("Attendee");
        attendee.setPhoneNumber("1234567890");
        attendee.setDateOfBirth(LocalDate.of(1991,4,10));
        attendee.setAddress(new Address("city", "state", "zip", "country"));
        attendee.setUserStatus(UserStatus.ACTIVE);
        attendee.setActive(true);

        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.ROLE_ATTENDEE);
        attendee.setRoles(roles);

        return attendee;
    }

    private Organizer createTestOrganizer(PasswordEncoder passwordEncoder) {
        Organizer organizer = new Organizer();
        organizer.setEmail("test.organizer@example.com");
        organizer.setPassword(passwordEncoder.encode("password"));
        organizer.setFirstName("Test");
        organizer.setLastName("Organizer");
        organizer.setOrganizationName("Test Organization");
        organizer.setDescription("Test Description");
        organizer.setWebsite("http://www.test.com");
        organizer.setContactPhone("123457890");
        organizer.setBankAccountInfo("Test Bankaccount Info");
        organizer.setCompanyRegistrationNumber("Test Company Registration Number");

        organizer.setUserStatus(UserStatus.ACTIVE);
        organizer.setActive(true);

        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.ROLE_ORGANIZER);
        organizer.setRoles(roles);

        return organizer;
    }

    private void createTokenForUser(
            de.tim.evenmanagmentsystem.user.model.User user,
            String tokenString,
            boolean expired,
            boolean revoked,
            TokenRepository tokenRepository) {

        Token token = Token.builder()
                .user(user)
                .token(tokenString)
                .tokenType(TokenType.BEARER)
                .expired(expired)
                .revoked(revoked)
                .expiresAt(expired ? LocalDateTime.now().minusDays(1) : LocalDateTime.now().plusDays(1))
                .build();

        tokenRepository.save(token);
    }
}