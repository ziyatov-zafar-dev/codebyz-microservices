
package uz.codebyz.message.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "private_chats",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user1_id", "user2_id"}))
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "user1_id", nullable = false)
    private UUID user1Id;
    @Column(name = "user2_id", nullable = false)
    private UUID user2Id;
    private String lastMessageId;
    private Instant lastMessageTime;
    private boolean mutedByUser1 = false;
    private boolean mutedByUser2 = false;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();


    private UUID deletedUser1Id;
    private UUID deletedUser2Id;
    private UUID blockUser1Id;
    private UUID blockUser2Id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(UUID user1Id) {
        this.user1Id = user1Id;
    }

    public UUID getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(UUID user2Id) {
        this.user2Id = user2Id;
    }

    public String getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(String lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public Instant getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Instant lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public boolean isMutedByUser1() {
        return mutedByUser1;
    }

    public void setMutedByUser1(boolean mutedByUser1) {
        this.mutedByUser1 = mutedByUser1;
    }

    public boolean isMutedByUser2() {
        return mutedByUser2;
    }

    public void setMutedByUser2(boolean mutedByUser2) {
        this.mutedByUser2 = mutedByUser2;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UUID getDeletedUser1Id() {
        return deletedUser1Id;
    }

    public void setDeletedUser1Id(UUID deletedUser1Id) {
        this.deletedUser1Id = deletedUser1Id;
    }

    public UUID getDeletedUser2Id() {
        return deletedUser2Id;
    }

    public void setDeletedUser2Id(UUID deletedUser2Id) {
        this.deletedUser2Id = deletedUser2Id;
    }

    public UUID getBlockUser1Id() {
        return blockUser1Id;
    }

    public void setBlockUser1Id(UUID blockUser1Id) {
        this.blockUser1Id = blockUser1Id;
    }

    public UUID getBlockUser2Id() {
        return blockUser2Id;
    }

    public void setBlockUser2Id(UUID blockUser2Id) {
        this.blockUser2Id = blockUser2Id;
    }
}
