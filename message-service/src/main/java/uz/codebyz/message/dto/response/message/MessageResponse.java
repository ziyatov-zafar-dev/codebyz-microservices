package uz.codebyz.message.dto.response.message;

import uz.codebyz.message.mongo.enums.MessageType;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class MessageResponse {
    private String id;
    private String chatId;

    private String senderId;
    private String content;
    private MessageType type;
    private String fileName;
    private String filePath;
    private String fileUrl;
    private String fileSizeMB;
    private Long fileSize;
    private Instant createdAt;
    private boolean edited;
    private Instant editedAt;
    private boolean read;
    private Instant readAt;
    private String replyToMessageId;
    private Map<String, List<String>> reactions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileSizeMB() {
        return fileSizeMB;
    }

    public void setFileSizeMB(String fileSizeMB) {
        this.fileSizeMB = fileSizeMB;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
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

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Instant getReadAt() {
        return readAt;
    }

    public void setReadAt(Instant readAt) {
        this.readAt = readAt;
    }

    public String getReplyToMessageId() {
        return replyToMessageId;
    }

    public void setReplyToMessageId(String replyToMessageId) {
        this.replyToMessageId = replyToMessageId;
    }

    public Map<String, List<String>> getReactions() {
        return reactions;
    }

    public void setReactions(Map<String, List<String>> reactions) {
        this.reactions = reactions;
    }

    public MessageResponse(String id, String chatId, String senderId, String content, MessageType type, String fileName, String filePath, String fileUrl, String fileSizeMB, Long fileSize, Instant createdAt, boolean edited, Instant editedAt, boolean read, Instant readAt, String replyToMessageId, Map<String, List<String>> reactions) {
        this.id = id;
        this.chatId = chatId;
        this.senderId = senderId;
        this.content = content;
        this.type = type;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileUrl = fileUrl;
        this.fileSizeMB = fileSizeMB;
        this.fileSize = fileSize;
        this.createdAt = createdAt;
        this.edited = edited;
        this.editedAt = editedAt;
        this.read = read;
        this.readAt = readAt;
        this.replyToMessageId = replyToMessageId;
        this.reactions = reactions;
    }

    public MessageResponse() {
    }
}
