package com.th.eventmanagmentsystem.common;

import jakarta.persistence.*;
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
public abstract class BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @LastModifiedBy
    @Column(name = "modified_by", nullable = false, length = 50)
    private String modifiedBy = "system";

    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false, length = 50)
    private String createdBy = "system";

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    @Column(name = "uuid", nullable = false, updatable = false, unique = true)
    private String uuid = UUID.randomUUID().toString();

    @Version
    private Long version;

    protected void deactivate() {
        this.active = false;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdBy == null || this.createdBy.isEmpty()) {
            this.createdBy = "system";
        }
        if (this.active == null) {
            this.active = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (this.modifiedBy == null || this.modifiedBy.isEmpty()) {
            this.modifiedBy = "system";
        }
    }

    // Getter (keine Setter für technische Felder)
    public Long getId() { return id; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public String getModifiedBy() { return modifiedBy; }
    public String getCreatedBy() { return createdBy; }
    public Boolean isActive() { return active; }
    public String getUuid() { return uuid; }
    public Long getVersion() { return version; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity that)) return false;
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
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", modifiedBy='" + modifiedBy + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", active=" + active +
                ", uuid='" + uuid + '\'' +
                ", version=" + version +
                '}';
    }
}
