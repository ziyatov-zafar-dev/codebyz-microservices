package uz.codebyz.message.dto.command;

import java.util.UUID;

public class EventAckCommand {
    private UUID chatId;
    private long lastEventSeq;

    
    public UUID getChatId() {
        return chatId;
    }

    public void setChatId(UUID chatId) {
        this.chatId = chatId;
    }

    public long getLastEventSeq() {
        return lastEventSeq;
    }

    public void setLastEventSeq(long lastEventSeq) {
        this.lastEventSeq = lastEventSeq;
    }
}
