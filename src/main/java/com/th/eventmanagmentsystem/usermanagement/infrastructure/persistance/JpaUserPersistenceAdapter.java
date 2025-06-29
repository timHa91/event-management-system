package com.th.eventmanagmentsystem.usermanagement.infrastructure.persistance;

import com.th.eventmanagmentsystem.usermanagement.domain.User;
import com.th.eventmanagmentsystem.usermanagement.domain.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserPersistenceAdapter extends UserRepository, JpaRepository<User, Long> {

}
