package com.th.eventmanagmentsystem.usermanagement.domain.model;

import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.regex.Pattern;

@Embeddable
public record Address (
        String street,
        String houseNumber,
        String postalCode,
        String city,
        String country
) {

    public Address {
        validate(street, "Straße");
        validate(houseNumber, "Hausnummer");
        validate(city, "Stadt");
        validate(country, "Land");
        validatePostalCode(postalCode);
    }

    public static Address of(String street, String houseNumber, String postalCode, String city, String country) {
        String trimmedStreet = (street == null) ? null : street.trim();
        String trimmedCity = (city == null) ? null : city.trim();
        String trimmedHouseNumber = (houseNumber == null) ? null : houseNumber.trim();
        String trimmedPostalCode = (postalCode == null) ? null: postalCode.trim();
        String trimmedCountry = (country == null) ? null: country.trim();
        return new Address(
                trimmedStreet,
                trimmedHouseNumber,
                trimmedPostalCode,
                trimmedCity,
                trimmedCountry
        );
    }

    private static void validate(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " darf nicht null sein");
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " darf nicht leer sein");
        }
    }

    private static void validatePostalCode(String postalCode) {
        Objects.requireNonNull(postalCode, "Postleitzahl darf nicht null sein");

        if (!Pattern.matches("^[0-9]{5}$", postalCode)) {
            throw new IllegalArgumentException("PLZ muss aus 5 Ziffern bestehen.");
        }
    }

    public String getFormattedAddress() {
        return String.format("%s %s, %s %s, %s",
                street, houseNumber, postalCode, city, country);
    }
}
