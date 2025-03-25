package de.tim.evenmanagmentsystem.common.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class) // Übernimmt die Aktualisierung der Zeitstempel
public abstract class BaseEntity implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @NotNull
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @NotBlank
    @LastModifiedBy
    @Column(name = "modified_by", length = 50)
    private String modifiedBy = "system";

    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy = "system"; // Standardwert

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "uuid", nullable = false, unique = true, updatable = false)
    private String uuid = UUID.randomUUID().toString(); //

    @Version
    private Long version;

    // Fallback-Initialisierung
    @PrePersist
    protected void onCreate() {
        var now = LocalDateTime.now();
        if (createdAt == null) {
           createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
        if (modifiedBy == null || modifiedBy.trim().isEmpty()) {
            modifiedBy = "system";
        }
        if (createdBy == null || createdBy.trim().isEmpty()) {
            createdBy = "system";
        }
    }

    @PreUpdate
    protected void onPreUpdate() {
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (modifiedBy == null || modifiedBy.trim().isEmpty()) {
            modifiedBy = "system";
        }
    }

    protected BaseEntity() {
    }

    // Softdelete anstatt echter Löschung
    public void deactivate() {
        this.active = false;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setModifiedBy(@NotBlank String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public void setUpdatedAt(@NotNull LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public boolean isActive() {
        return active;
    }

    public String getUuid() {
        return uuid;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof BaseEntity that)) return false;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
