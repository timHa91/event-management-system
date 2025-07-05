package com.th.eventmanagmentsystem.usermanagement.application.mapper;

import com.th.eventmanagmentsystem.usermanagement.application.dto.UserRegistrationResponse;
import com.th.eventmanagmentsystem.usermanagement.domain.model.User;
import com.th.eventmanagmentsystem.usermanagement.domain.model.UserStatus;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-05T12:44:45+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserRegistrationResponse toResponse(User user) {
        if ( user == null ) {
            return null;
        }

        String uuid = null;
        UserStatus status = null;

        uuid = user.getUuid();
        status = user.getStatus();

        String email = user.getEmail().email();

        UserRegistrationResponse userRegistrationResponse = new UserRegistrationResponse( uuid, email, status );

        return userRegistrationResponse;
    }
}
