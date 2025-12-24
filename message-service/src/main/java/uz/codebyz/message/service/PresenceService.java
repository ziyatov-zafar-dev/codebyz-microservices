package uz.codebyz.message.service;

import org.springframework.stereotype.Service;
import uz.codebyz.message.domain.EventEnvelope;
import uz.codebyz.message.domain.EventType;
import uz.codebyz.message.security.JwtUser;
import uz.codebyz.message.ws.EventBroadcaster;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PresenceService {

    private final ChatDirectory chatDirectory;
    private final EventBroadcaster broadcaster;
    private final ConcurrentHashMap<UUID, Boolean> online = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Instant> lastSeen = new ConcurrentHashMap<>();

    public PresenceService(ChatDirectory chatDirectory, EventBroadcaster broadcaster) {
        this.chatDirectory = chatDirectory;
        this.broadcaster = broadcaster;
    }

    public void online(JwtUser user) {
        publishPresence(user.getUserId(), EventType.USER_ONLINE);
        online.put(user.getUserId(), true);
        lastSeen.put(user.getUserId(), Instant.now());
    }

    public void offline(JwtUser user) {
        publishPresence(user.getUserId(), EventType.USER_OFFLINE);
        online.put(user.getUserId(), false);
        lastSeen.put(user.getUserId(), Instant.now());
    }

    private void publishPresence(UUID userId, EventType type) {
        Set<UUID> chatIds = chatDirectory.chatIdsOf(userId);
        for (UUID chatId : chatIds) {
            EventEnvelope envelope = new EventEnvelope(
                    UUID.randomUUID(),
                    type,
                    chatId,
                    chatDirectory.find(chatId).map(ChatDirectory.ChatState::nextSeq).orElse(0L),
                    userId,
                    Instant.now(),
                    null,
                    null
            );
            chatDirectory.find(chatId).ifPresent(chat -> {
                broadcaster.broadcastToChat(envelope);
            });
        }
    }

    public boolean isOnline(UUID userId) {
        return online.getOrDefault(userId, false);
    }

    public Instant getLastSeen(UUID userId) {
        return lastSeen.get(userId);
    }
}
