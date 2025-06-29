package com.th.eventmanagmentsystem.usermanagement.application.config;

import com.th.eventmanagmentsystem.usermanagement.application.dto.UserRegistrationRequest;
import com.th.eventmanagmentsystem.usermanagement.domain.policy.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RegistrationPolicyConfig {

    @Bean("defaultRegistrationPolicies")
    public List<RegistrationPolicy<UserRegistrationRequest>> defaultRegistrationPolicies(
            UniqueEmailPolicy uniqueEmailPolicy,
            StrongPasswordPolicy strongPasswordPolicy,
            EmailFormatPolicy emailFormatPolicy
    ) {
        return List.of(uniqueEmailPolicy, strongPasswordPolicy, emailFormatPolicy);
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
