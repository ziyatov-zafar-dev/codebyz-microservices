package uz.codebyz.message.dto.payload;

import uz.codebyz.message.domain.SystemAction;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class SystemMessagePayload {
    private UUID systemMessageId;
    private SystemAction action;
    private String textKey;
    private Map<String, Object> params;
    private Instant createdAt;

    public UUID getSystemMessageId() {
        return systemMessageId;
    }

    public void setSystemMessageId(UUID systemMessageId) {
        this.systemMessageId = systemMessageId;
    }

    public SystemAction getAction() {
        return action;
    }

    public void setAction(SystemAction action) {
        this.action = action;
    }

    public String getTextKey() {
        return textKey;
    }

    public void setTextKey(String textKey) {
        this.textKey = textKey;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
