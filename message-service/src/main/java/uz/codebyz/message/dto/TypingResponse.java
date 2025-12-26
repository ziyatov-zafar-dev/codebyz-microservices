package uz.codebyz.message.dto;

import java.util.UUID;

public class TypingResponse {
    private UUID senderId;
    private boolean typing;

    public TypingResponse(UUID senderId, boolean typing) {
        this.senderId = senderId;
        this.typing = typing;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public boolean isTyping() {
        return typing;
    }
}
