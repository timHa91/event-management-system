package com.th.eventmanagmentsystem.usermanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Address {

    @NotBlank
    @Column(name = "street", nullable = false)
    private String street;

    @NotBlank
    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    @NotBlank
    @Column(name = "city", nullable = false)
    private String city;

    @NotBlank
    @Column(name = "country", nullable = false)
    private String country;

    public String getFormatedAddress() {
        return String.format("%s, %s, %s, %s", street, city, country, zipCode);
    }

}
