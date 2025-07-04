package com.th.eventmanagmentsystem.usermanagement.infrastructure.persistance.converter;

import com.th.eventmanagmentsystem.usermanagement.domain.model.UserPassword;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserPasswordConverter implements AttributeConverter<UserPassword, String> {
    @Override
    public String convertToDatabaseColumn(UserPassword password) {
        return password == null ? null : password.hashedPassword();
    }

    @Override
    public UserPassword convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new UserPassword(dbData);
    }
}
