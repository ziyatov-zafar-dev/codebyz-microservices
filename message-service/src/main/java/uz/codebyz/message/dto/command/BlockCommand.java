package uz.codebyz.message.dto.command;

import java.util.UUID;

public class BlockCommand {
    private UUID chatId;
    private boolean block;

    public UUID getChatId() {
        return chatId;
    }

    public void setChatId(UUID chatId) {
        this.chatId = chatId;
    }

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }
}
