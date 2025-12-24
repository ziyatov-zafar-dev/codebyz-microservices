package uz.codebyz.message.service;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TypingRegistry {
    private final ConcurrentHashMap<UUID, Set<UUID>> typingByChat = new ConcurrentHashMap<>();

    public void start(UUID chatId, UUID userId) {
        typingByChat.computeIfAbsent(chatId, k -> ConcurrentHashMap.newKeySet()).add(userId);
    }

    public void stop(UUID chatId, UUID userId) {
        typingByChat.computeIfPresent(chatId, (k, v) -> {
            v.remove(userId);
            return v;
        });
    }

    public Set<UUID> who(UUID chatId) {
        return typingByChat.getOrDefault(chatId, Collections.emptySet());
    }
}
