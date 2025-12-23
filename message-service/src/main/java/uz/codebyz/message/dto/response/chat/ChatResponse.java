package uz.codebyz.message.dto.response.chat;

import uz.codebyz.message.dto.response.message.MessageResponse;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ChatResponse {
    private UUID id;
    private UUID user1Id;
    private UUID user2Id;
    private String lastMessageId;
    private Instant lastMessageTime;
    private boolean mutedByUser1 = false;
    private boolean mutedByUser2 = false;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID blockUser1Id;
    private UUID blockUser2Id;

    private List<MessageResponse> messages;

    public ChatResponse() {
    }

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

    public ChatResponse(UUID id, UUID user1Id, UUID user2Id, String lastMessageId,
                        Instant lastMessageTime, boolean mutedByUser1,
                        boolean mutedByUser2, Instant createdAt, Instant updatedAt,
                        UUID blockUser1Id, UUID blockUser2Id,List<MessageResponse> messages) {
        this.id = id;
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.lastMessageId = lastMessageId;
        this.lastMessageTime = lastMessageTime;
        this.mutedByUser1 = mutedByUser1;
        this.mutedByUser2 = mutedByUser2;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.blockUser1Id = blockUser1Id;
        this.blockUser2Id = blockUser2Id;
    }

    public List<MessageResponse> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageResponse> messages) {
        this.messages = messages;
    }
}
