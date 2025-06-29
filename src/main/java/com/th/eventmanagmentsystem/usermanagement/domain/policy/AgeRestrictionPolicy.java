package com.th.eventmanagmentsystem.usermanagement.domain.policy;

import com.th.eventmanagmentsystem.usermanagement.application.dto.UserRegistrationRequest;
import org.springframework.stereotype.Component;

@Component
public class AgeRestrictionPolicy implements RegistrationPolicy<UserRegistrationRequest> {


    @Override
    public void check(UserRegistrationRequest request) {

    }
}
