package com.th.eventmanagmentsystem.usermanagement.domain.policy;

import com.th.eventmanagmentsystem.usermanagement.application.dto.UserRegistrationRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("defaultUserRegistrationPolicyAggregator")
public class DefaultUserRegistrationPolicyAggregator implements RegistrationPolicy<UserRegistrationRequest> {

    private final List<RegistrationPolicy<UserRegistrationRequest>> policies;

    public DefaultUserRegistrationPolicyAggregator(
            @Qualifier("defaultRegistrationPolicies")
            List<RegistrationPolicy<UserRegistrationRequest>> policies) {
        this.policies = policies;
    }

    @Override
    public void check(UserRegistrationRequest request) {
        policies.forEach(policy -> policy.check(request));
    }
}
