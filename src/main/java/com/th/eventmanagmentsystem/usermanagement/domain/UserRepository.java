package com.th.eventmanagmentsystem.usermanagement.domain;

import java.util.Optional;

public interface UserRepository {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
    User save(User user);
}
