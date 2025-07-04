package com.th.eventmanagmentsystem.usermanagement.application.mapper;

import com.th.eventmanagmentsystem.usermanagement.application.dto.UserRegistrationRequest;
import com.th.eventmanagmentsystem.usermanagement.application.dto.UserRegistrationResponse;
import com.th.eventmanagmentsystem.usermanagement.domain.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface UserMapper {

    default User toUser(UserRegistrationRequest request, String hashedPassword) {
        EmailAddress email = EmailAddress.of(request.email());
        UserPassword password = UserPassword.of(hashedPassword);

        return new User(
                email,
                password,
                UserStatus.INACTIVE,
                Set.of(UserRole.ROLE_USER)
        );
    }

    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "email", expression = "java(user.getEmail().email())")
    @Mapping(target = "status", source = "status")
    UserRegistrationResponse toResponse(User user);
}
