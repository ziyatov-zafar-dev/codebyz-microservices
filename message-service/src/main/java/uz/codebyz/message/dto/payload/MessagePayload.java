package uz.codebyz.message.dto.payload;

import uz.codebyz.message.domain.MessageType;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class MessagePayload {
    private UUID messageId;
    private UUID tempMessageId;
    private UUID senderId;
    private UUID chatId;
    private String content;
    private MessageType type;
    private FileMeta file;
    private String replyToMessageId;
    private String forwardFromMessageId;
    private Map<String, java.util.List<String>> reactions;
    private Instant createdAt;
    private boolean edited;
    private Instant editedAt;
    private String status;
    private boolean system;
    private boolean deletedForAll;
    private java.util.Set<UUID> deletedForUsers = new java.util.HashSet<>();
    private java.util.Set<UUID> deliveredTo = new java.util.HashSet<>();
    private java.util.Set<UUID> readBy = new java.util.HashSet<>();

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public UUID getTempMessageId() {
        return tempMessageId;
    }

    public void setTempMessageId(UUID tempMessageId) {
        this.tempMessageId = tempMessageId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public UUID getChatId() {
        return chatId;
    }

    public void setChatId(UUID chatId) {
        this.chatId = chatId;
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

    public String getForwardFromMessageId() {
        return forwardFromMessageId;
    }

    public void setForwardFromMessageId(String forwardFromMessageId) {
        this.forwardFromMessageId = forwardFromMessageId;
    }

    public Map<String, java.util.List<String>> getReactions() {
        return reactions;
    }

    public void setReactions(Map<String, java.util.List<String>> reactions) {
        this.reactions = reactions;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public Instant getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(Instant editedAt) {
        this.editedAt = editedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    public boolean isDeletedForAll() {
        return deletedForAll;
    }

    public void setDeletedForAll(boolean deletedForAll) {
        this.deletedForAll = deletedForAll;
    }

    public java.util.Set<UUID> getDeletedForUsers() {
        return deletedForUsers;
    }

    public void setDeletedForUsers(java.util.Set<UUID> deletedForUsers) {
        this.deletedForUsers = deletedForUsers;
    }

    public java.util.Set<UUID> getDeliveredTo() {
        return deliveredTo;
    }

    public void setDeliveredTo(java.util.Set<UUID> deliveredTo) {
        this.deliveredTo = deliveredTo;
    }

    public java.util.Set<UUID> getReadBy() {
        return readBy;
    }

    public void setReadBy(java.util.Set<UUID> readBy) {
        this.readBy = readBy;
    }

    public static class FileMeta {
        private String name;
        private String url;
        private Long size;
        private String sizeReadable;

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

        public String getSizeReadable() {
            return sizeReadable;
        }

        public void setSizeReadable(String sizeReadable) {
            this.sizeReadable = sizeReadable;
        }
    }
}
