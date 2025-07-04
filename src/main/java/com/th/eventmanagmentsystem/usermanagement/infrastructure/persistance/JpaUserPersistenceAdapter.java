package com.th.eventmanagmentsystem.usermanagement.infrastructure.persistance;

import com.th.eventmanagmentsystem.usermanagement.domain.model.User;
import com.th.eventmanagmentsystem.usermanagement.domain.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserPersistenceAdapter extends UserRepository, JpaRepository<User, Long> {

}
