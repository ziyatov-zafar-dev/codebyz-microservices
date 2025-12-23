package uz.codebyz.auth.guard;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_login_guard",
        uniqueConstraints = { @UniqueConstraint(name = "uk_login_guard_user", columnNames = {"userId"}) }
)
public class UserLoginGuard {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(nullable = false)
    private int failCount;

    @Column(nullable = false)
    private Instant blockedUntil;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        if (updatedAt == null) updatedAt = Instant.now();
        if (blockedUntil == null) blockedUntil = Instant.EPOCH;
    }

    @PreUpdate
    public void preUpdate() { updatedAt = Instant.now(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public int getFailCount() { return failCount; }
    public void setFailCount(int failCount) { this.failCount = failCount; }
    public Instant getBlockedUntil() { return blockedUntil; }
    public void setBlockedUntil(Instant blockedUntil) { this.blockedUntil = blockedUntil; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
