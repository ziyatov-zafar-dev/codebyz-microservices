package uz.codebyz.message.dto.command;

import java.util.UUID;

public class TypingCommand {
    private UUID chatId;
    private boolean typing;

    public UUID getChatId() {
        return chatId;
    }

    public void setChatId(UUID chatId) {
        this.chatId = chatId;
    }

    public boolean isTyping() {
        return typing;
    }

    public void setTyping(boolean typing) {
        this.typing = typing;
    }
}
