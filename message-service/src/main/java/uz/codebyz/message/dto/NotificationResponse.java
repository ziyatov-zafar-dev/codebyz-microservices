package uz.codebyz.message.dto;

import java.time.Instant;
import java.util.UUID;

public class NotificationResponse {
    private String type;
    private String messageId;
    private UUID chatId;
    private UUID senderId;
    private UUID receiverId;
    private String content;
    private Instant createdAt;

    public NotificationResponse(String type, String messageId, UUID chatId, UUID senderId, UUID receiverId, String content, Instant createdAt) {
        this.type = type;
        this.messageId = messageId;
        this.chatId = chatId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public String getType() {
        return type;
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
}
