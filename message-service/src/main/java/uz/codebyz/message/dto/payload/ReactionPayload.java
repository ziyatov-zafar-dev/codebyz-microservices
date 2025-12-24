package uz.codebyz.message.dto.payload;

import java.util.Map;
import java.util.UUID;

public class ReactionPayload {
    private UUID chatId;
    private UUID messageId;
    private String emoji;
    private UUID userId;
    private Map<String, java.util.List<String>> reactions;

    public UUID getChatId() {
        return chatId;
    }

    public void setChatId(UUID chatId) {
        this.chatId = chatId;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Map<String, java.util.List<String>> getReactions() {
        return reactions;
    }

    public void setReactions(Map<String, java.util.List<String>> reactions) {
        this.reactions = reactions;
    }
}
