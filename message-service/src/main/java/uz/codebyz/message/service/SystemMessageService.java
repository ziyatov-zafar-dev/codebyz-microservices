package uz.codebyz.message.service;

import org.springframework.stereotype.Service;
import uz.codebyz.message.domain.EventEnvelope;
import uz.codebyz.message.domain.EventType;
import uz.codebyz.message.domain.SystemAction;
import uz.codebyz.message.dto.payload.SystemMessagePayload;
import uz.codebyz.message.ws.EventBroadcaster;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SystemMessageService {

    private final EventBroadcaster broadcaster;

    public SystemMessageService(EventBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    public void systemForAction(ChatDirectory.ChatState chat, SystemAction action) {
        SystemMessagePayload payload = new SystemMessagePayload();
        payload.setSystemMessageId(UUID.randomUUID());
        payload.setAction(action);
        payload.setTextKey(action.name());
        payload.setParams(new HashMap<>());
        payload.setCreatedAt(Instant.now());

        EventEnvelope envelope = new EventEnvelope(
                UUID.randomUUID(),
                EventType.SYSTEM_MESSAGE_CREATED,
                chat.getChatId(),
                chat.nextSeq(),
                null,
                Instant.now(),
                null,
                payload
        );
        broadcaster.broadcastToChat(envelope);
    }

    public void systemInfo(ChatDirectory.ChatState chat, String textKey, Map<String, Object> params) {
        SystemMessagePayload payload = new SystemMessagePayload();
        payload.setSystemMessageId(UUID.randomUUID());
        payload.setAction(SystemAction.INFO);
        payload.setTextKey(textKey);
        payload.setParams(params == null ? new HashMap<>() : params);
        payload.setCreatedAt(Instant.now());

        EventEnvelope envelope = new EventEnvelope(
                UUID.randomUUID(),
                EventType.SYSTEM_MESSAGE_CREATED,
                chat.getChatId(),
                chat.nextSeq(),
                null,
                Instant.now(),
                null,
                payload
        );
        broadcaster.broadcastToChat(envelope);
    }
}
