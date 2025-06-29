package com.th.eventmanagmentsystem.usermanagement.application.config;

import com.th.eventmanagmentsystem.usermanagement.application.dto.UserRegistrationRequest;
import com.th.eventmanagmentsystem.usermanagement.domain.policy.AgeRestrictionPolicy;
import com.th.eventmanagmentsystem.usermanagement.domain.policy.RegistrationPolicy;
import com.th.eventmanagmentsystem.usermanagement.domain.policy.StrongPasswordPolicy;
import com.th.eventmanagmentsystem.usermanagement.domain.policy.UniqueEmailPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RegistrationPolicyConfig {

    @Bean("defaultRegistrationPolicies")
    public List<RegistrationPolicy<UserRegistrationRequest>> defaultRegistrationPolicies(
            UniqueEmailPolicy uniqueEmailPolicy,
            StrongPasswordPolicy strongPasswordPolicy
    ) {
        return List.of(uniqueEmailPolicy, strongPasswordPolicy);
    }

    @Bean("strongRegistrationPolicies")
    public List<RegistrationPolicy<UserRegistrationRequest>> strongRegistrationPolicies(
            UniqueEmailPolicy uniqueEmailPolicy,
            StrongPasswordPolicy strongPasswordPolicy,
            AgeRestrictionPolicy ageRestrictionPolicy
    ) {
        return List.of(uniqueEmailPolicy, strongPasswordPolicy, ageRestrictionPolicy);
    }

}
