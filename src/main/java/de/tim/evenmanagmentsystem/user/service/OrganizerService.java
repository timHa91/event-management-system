package de.tim.evenmanagmentsystem.user.service;

import de.tim.evenmanagmentsystem.user.dto.OrganizerRegistrationDTO;
import de.tim.evenmanagmentsystem.user.model.Organizer;
import de.tim.evenmanagmentsystem.user.model.UserRole;
import de.tim.evenmanagmentsystem.user.model.UserStatus;
import de.tim.evenmanagmentsystem.user.repository.OrganizerRepository;
import de.tim.evenmanagmentsystem.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OrganizerService extends UserService{

    private final OrganizerRepository organizerRepository;

    public OrganizerService(UserRepository userRepository, PasswordEncoder passwordEncoder, OrganizerRepository organizerRepository) {
        super(userRepository, passwordEncoder);
        this.organizerRepository = organizerRepository;
    }

    public Organizer register(OrganizerRegistrationDTO registrationDTO) {

        Organizer organizer = new Organizer(
                registrationDTO.getEmail(),
                registrationDTO.getPassword(),
                registrationDTO.getFirstName(),
                registrationDTO.getLastName(),
                registrationDTO.getOrganizationName(),
                registrationDTO.getDescription(),
                registrationDTO.getCompanyRegistrationNumber(),
                registrationDTO.getBankAccountInfo()
        );

        organizer.setActive(true);
        organizer.setUserStatus(UserStatus.ACTIVE);

        organizer.addRole(UserRole.ROLE_USER);
        organizer.addRole(UserRole.ROLE_ORGANIZER);

        return organizerRepository.save(organizer);
    }
}
