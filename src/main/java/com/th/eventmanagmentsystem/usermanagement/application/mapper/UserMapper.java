package com.th.eventmanagmentsystem.usermanagement.application.mapper;

import com.th.eventmanagmentsystem.usermanagement.application.dto.UserRegistrationRequest;
import com.th.eventmanagmentsystem.usermanagement.application.dto.UserRegistrationResponse;
import com.th.eventmanagmentsystem.usermanagement.domain.User;
import com.th.eventmanagmentsystem.usermanagement.domain.UserProfile;
import com.th.eventmanagmentsystem.usermanagement.domain.UserRole;
import com.th.eventmanagmentsystem.usermanagement.domain.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mappings({
            @Mapping(source = "request.email", target = "email"),

            @Mapping(source = "password", target = "password"),
            @Mapping(source = "status", target = "status"),
            @Mapping(source = "roles", target = "roles")
    })
    User requestToUser(
            UserRegistrationRequest request,
            String password,
            UserProfile userProfile,
            UserStatus status,
            Set<UserRole> roles
    );

    @Mappings({
            @Mapping(source = "user.uuid", target = "uuid"),
            @Mapping(source = "user.email", target = "email"),
            @Mapping(source = "user.status", target = "status")
    })
    UserRegistrationResponse userToResponse(User user);
}
