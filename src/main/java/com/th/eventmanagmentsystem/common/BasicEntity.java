package com.th.eventmanagmentsystem.common;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BasicEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @LastModifiedBy
    @NotBlank
    @Size(max = 50)
    @Column(name = "modified_by", nullable = false, length = 50)
    private String modifiedBy;

    @CreatedBy
    @NotBlank
    @Size(max = 50)
    @Column(name = "created_by", nullable = false, updatable = false, length = 50)
    private String createdBy;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @NotBlank
    @Column(name = "uuid", nullable = false, updatable = false, unique = true)
    private String uuid = UUID.randomUUID().toString();

    @Version
    private Long version;

    protected void deactivate() {
        this.isActive = false;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdBy == null || this.createdBy.isEmpty()) {
            this.createdBy = "system";
        }

        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (this.modifiedBy == null || this.modifiedBy.isEmpty()) {
            this.modifiedBy = "system";
        }
    }

    protected BasicEntity() {
    }

    public void setUpdatedAt(Instant updatedAt) {
        if (updatedAt == null) {
            updatedAt = Instant.now();
        }
        this.updatedAt = updatedAt;
    }


    public void setModifiedBy(@NotBlank @Size(max = 50) String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public void setActive(@NotNull Boolean active) {
        isActive = active;
    }

    public Long getId() {
        return id;
    }

    public @NotNull Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public @NotBlank @Size(max = 50) String getModifiedBy() {
        return modifiedBy;
    }

    public @NotBlank @Size(max = 50) String getCreatedBy() {
        return createdBy;
    }

    public @NotNull Boolean isActive() {
        return isActive;
    }

    public @NotBlank String getUuid() {
        return uuid;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasicEntity that)) return false;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }

    @Override
    public String toString() {
        return "BasicEntity{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", modifiedBy='" + modifiedBy + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", isActive=" + isActive +
                ", uuid='" + uuid + '\'' +
                ", version=" + version +
                '}';
    }
}
