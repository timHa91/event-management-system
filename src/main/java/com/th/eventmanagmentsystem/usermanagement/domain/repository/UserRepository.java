package com.th.eventmanagmentsystem.usermanagement.domain.repository;

import com.th.eventmanagmentsystem.usermanagement.domain.model.EmailAddress;
import com.th.eventmanagmentsystem.usermanagement.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    boolean existsByEmail(EmailAddress email);

    List<User> findAll();

    Optional<User> findByEmail(EmailAddress email);
    User save(User user);
}
