package uz.codebyz.message.dto;

import java.time.Instant;
import java.util.UUID;

public class ChatResponse {
    private UUID chatId;
    private UUID user1Id;
    private UUID user2Id;
    private Instant createdAt;
    private Instant lastMessageAt;

    public ChatResponse(UUID chatId, UUID user1Id, UUID user2Id, Instant createdAt, Instant lastMessageAt) {
        this.chatId = chatId;
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.createdAt = createdAt;
        this.lastMessageAt = lastMessageAt;
    }

    public UUID getChatId() {
        return chatId;
    }

    public UUID getUser1Id() {
        return user1Id;
    }

    public UUID getUser2Id() {
        return user2Id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getLastMessageAt() {
        return lastMessageAt;
    }
}
