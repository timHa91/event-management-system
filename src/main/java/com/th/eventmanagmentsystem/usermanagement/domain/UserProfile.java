package com.th.eventmanagmentsystem.usermanagement.domain;

import com.th.eventmanagmentsystem.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@NoArgsConstructor
@Getter
public abstract class UserProfile extends BaseEntity {

    @NotNull
    @OneToOne(mappedBy = "profile")
    private User user;

    protected void setUser(User user) {
        Objects.requireNonNull(user, "User cannot be null");

        if (this.user != null && !this.user.equals(user)) {
            throw new IllegalStateException("This profile is already assigned to another user");
        }

        if (user.getProfile() != null && !user.getProfile().equals(this)) {
            throw new IllegalArgumentException("User already has a different User Profile");
        }

        this.user = user;
    }
}
