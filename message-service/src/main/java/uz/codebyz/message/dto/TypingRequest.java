package uz.codebyz.message.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class TypingRequest {
    @NotNull
    private UUID receiverId;
    private boolean typing;

    public UUID getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(UUID receiverId) {
        this.receiverId = receiverId;
    }

    public boolean isTyping() {
        return typing;
    }

    public void setTyping(boolean typing) {
        this.typing = typing;
    }
}
