package uz.codebyz.message.domain;

import java.time.Instant;
import java.util.UUID;

public class EventEnvelope {
    private UUID eventId;
    private EventType eventType;
    private UUID chatId;
    private long eventSeq;
    private UUID actorUserId;
    private Instant timestamp;
    private UUID clientEventId;
    private Object payload;

    public EventEnvelope() {
    }

    public EventEnvelope(UUID eventId, EventType eventType, UUID chatId, long eventSeq, UUID actorUserId, Instant timestamp, UUID clientEventId, Object payload) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.chatId = chatId;
        this.eventSeq = eventSeq;
        this.actorUserId = actorUserId;
        this.timestamp = timestamp;
        this.clientEventId = clientEventId;
        this.payload = payload;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public UUID getChatId() {
        return chatId;
    }

    public void setChatId(UUID chatId) {
        this.chatId = chatId;
    }

    public long getEventSeq() {
        return eventSeq;
    }

    public void setEventSeq(long eventSeq) {
        this.eventSeq = eventSeq;
    }

    public UUID getActorUserId() {
        return actorUserId;
    }

    public void setActorUserId(UUID actorUserId) {
        this.actorUserId = actorUserId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public UUID getClientEventId() {
        return clientEventId;
    }

    public void setClientEventId(UUID clientEventId) {
        this.clientEventId = clientEventId;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
