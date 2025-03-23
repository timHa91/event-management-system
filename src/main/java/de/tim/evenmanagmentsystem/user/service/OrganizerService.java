package de.tim.evenmanagmentsystem.user.service;

import de.tim.evenmanagmentsystem.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OrganizerService extends UserService{


    public OrganizerService(UserRepository userRepository) {
        super(userRepository);
    }
}
