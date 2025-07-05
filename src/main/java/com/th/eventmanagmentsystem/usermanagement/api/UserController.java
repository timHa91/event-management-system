package com.th.eventmanagmentsystem.usermanagement.api;

import com.th.eventmanagmentsystem.usermanagement.domain.model.User;
import com.th.eventmanagmentsystem.usermanagement.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping
    List<User> getAll() {
        return userRepository.findAll();
    }
}
