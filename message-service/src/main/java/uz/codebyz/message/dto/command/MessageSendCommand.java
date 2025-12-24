package uz.codebyz.message.dto.command;

import uz.codebyz.message.domain.MessageType;

import java.util.Map;
import java.util.UUID;

public class MessageSendCommand {
    private UUID clientEventId;
    private UUID tempMessageId;
    private UUID chatId;
    private UUID peerId;
    private String content;
    private MessageType type;
    private FileMeta file;
    private String replyToMessageId;
    private Map<String, java.util.List<String>> reactions;

    public UUID getClientEventId() {
        return clientEventId;
    }

    public void setClientEventId(UUID clientEventId) {
        this.clientEventId = clientEventId;
    }

    public UUID getTempMessageId() {
        return tempMessageId;
    }

    public void setTempMessageId(UUID tempMessageId) {
        this.tempMessageId = tempMessageId;
    }

    public UUID getChatId() {
        return chatId;
    }

    public void setChatId(UUID chatId) {
        this.chatId = chatId;
    }

    public UUID getPeerId() {
        return peerId;
    }

    public void setPeerId(UUID peerId) {
        this.peerId = peerId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public FileMeta getFile() {
        return file;
    }

    public void setFile(FileMeta file) {
        this.file = file;
    }

    public String getReplyToMessageId() {
        return replyToMessageId;
    }

    public void setReplyToMessageId(String replyToMessageId) {
        this.replyToMessageId = replyToMessageId;
    }

    public Map<String, java.util.List<String>> getReactions() {
        return reactions;
    }

    public void setReactions(Map<String, java.util.List<String>> reactions) {
        this.reactions = reactions;
    }

    public static class FileMeta {
        private String name;
        private String url;
        private Long size;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Long getSize() {
            return size;
        }

        public void setSize(Long size) {
            this.size = size;
        }
    }
}
