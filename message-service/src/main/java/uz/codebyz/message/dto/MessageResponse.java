package uz.codebyz.message.dto;

import java.time.Instant;
import java.util.UUID;

public class MessageResponse {
    private String messageId;
    private UUID chatId;
    private UUID senderId;
    private UUID receiverId;
    private String content;
    private Instant createdAt;
    private boolean read;

    public MessageResponse(String messageId, UUID chatId, UUID senderId, UUID receiverId, String content, Instant createdAt, boolean read) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.createdAt = createdAt;
        this.read = read;
    }

    public String getMessageId() {
        return messageId;
    }

    public UUID getChatId() {
        return chatId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public UUID getReceiverId() {
        return receiverId;
    }

    public String getContent() {
        return content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isRead() {
        return read;
    }
}
