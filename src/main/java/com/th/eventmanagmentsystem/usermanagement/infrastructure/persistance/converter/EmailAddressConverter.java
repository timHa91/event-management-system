package com.th.eventmanagmentsystem.usermanagement.infrastructure.persistance.converter;

import com.th.eventmanagmentsystem.usermanagement.domain.model.EmailAddress;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EmailAddressConverter implements AttributeConverter<EmailAddress, String> {

    @Override
    public String convertToDatabaseColumn(EmailAddress email) {
        return email == null ? null : email.email();
    }

    @Override
    public EmailAddress convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new EmailAddress(dbData);
    }
}
