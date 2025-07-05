package com.th.eventmanagmentsystem.usermanagement.domain.model;

import com.th.eventmanagmentsystem.common.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@Getter
public abstract class UserProfile extends BaseEntity {

//    @OneToOne(mappedBy = "profile")
//    private User user;
//
//
//    protected void setUser(User user) {
//        Objects.requireNonNull(user, "User cannot be null");
//
//        if (this.user != null && !this.user.equals(user)) {
//            throw new IllegalStateException("This profile is already assigned to another user");
//        }
//
//        if (user.getProfile() != null && !user.getProfile().equals(this)) {
//            throw new IllegalArgumentException("User already has a different User Profile");
//        }
//
//        this.user = user;
//    }
}
