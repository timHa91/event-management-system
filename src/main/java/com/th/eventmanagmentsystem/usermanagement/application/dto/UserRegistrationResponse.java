package com.th.eventmanagmentsystem.usermanagement.application.dto;

import com.th.eventmanagmentsystem.usermanagement.domain.model.UserStatus;

public record UserRegistrationResponse (
    String uuid,
    String email,
    UserStatus status
) {}