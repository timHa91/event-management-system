package de.tim.evenmanagmentsystem.user.service;

import de.tim.evenmanagmentsystem.user.dto.AttendeeRegistrationDTO;
import de.tim.evenmanagmentsystem.user.exception.EmailAlreadyExistsException;
import de.tim.evenmanagmentsystem.user.model.Attendee;
import de.tim.evenmanagmentsystem.user.model.UserRole;
import de.tim.evenmanagmentsystem.user.model.UserStatus;
import de.tim.evenmanagmentsystem.user.repository.AttendeeRepository;
import de.tim.evenmanagmentsystem.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AttendeeService extends UserService {

    private final AttendeeRepository attendeeRepository;

    public AttendeeService(UserRepository userRepository, PasswordEncoder passwordEncoder, AttendeeRepository attendeeRepository) {
        super(userRepository, passwordEncoder);
        this.attendeeRepository = attendeeRepository;
    }

    public Attendee register(AttendeeRegistrationDTO attendeeDTO) {
        if (!isEmailAvailable(attendeeDTO.getEmail())) {
            throw new EmailAlreadyExistsException("Email already in use");
        }

        Attendee attendee = new Attendee(
                attendeeDTO.getEmail(),
                encodePassword(attendeeDTO.getPassword()),
                attendeeDTO.getFirstName(),
                attendeeDTO.getLastName(),
                attendeeDTO.getPhoneNumber(),
                attendeeDTO.getDateOfBirth(),
                attendeeDTO.getAddress(),
                attendeeDTO.getCity(),
                attendeeDTO.getPostalCode(),
                attendeeDTO.getCountry()
        );

        attendee.setActive(true);
        attendee.setUserStatus(UserStatus.ACTIVE);

        attendee.addRole(UserRole.ROLE_USER);
        attendee.addRole(UserRole.ROLE_ATTENDEE);

        return attendeeRepository.save(attendee);
    }
}
