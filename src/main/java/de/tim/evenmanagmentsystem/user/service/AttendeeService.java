package de.tim.evenmanagmentsystem.user.service;

import de.tim.evenmanagmentsystem.user.repository.AttendeeRepository;
import de.tim.evenmanagmentsystem.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AttendeeService extends UserService {

    private final AttendeeRepository attendeeRepository;

    public AttendeeService(UserRepository userRepository, AttendeeRepository attendeeRepository) {
        super(userRepository);
        this.attendeeRepository = attendeeRepository;
    }


}
