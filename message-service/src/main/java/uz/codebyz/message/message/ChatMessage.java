package uz.codebyz.message.message;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document(collection = "messages")
public class ChatMessage {

    @Id
    private String id;
    private UUID chatId;
    private UUID senderId;
    private UUID receiverId;
    private String content;
    @CreatedDate
    private Instant createdAt;
    private boolean read;

    public ChatMessage() {
    }

    public ChatMessage(UUID chatId, UUID senderId, UUID receiverId, String content) {
        this.chatId = chatId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.createdAt = Instant.now();
        this.read = false;
    }

    public String getId() {
        return id;
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

    public void markRead() {
        this.read = true;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
