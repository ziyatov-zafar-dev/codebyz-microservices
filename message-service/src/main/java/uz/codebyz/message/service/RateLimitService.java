package uz.codebyz.message.service;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitService {

    private record Key(UUID userId, UUID chatId, String action) {}

    private final Map<Key, Instant> lastAction = new ConcurrentHashMap<>();

    public boolean allow(UUID userId, UUID chatId, String action, Duration minInterval) {
        Key key = new Key(userId, chatId, action);
        Instant now = Instant.now();
        Instant prev = lastAction.get(key);
        if (prev != null && Duration.between(prev, now).compareTo(minInterval) < 0) {
            return false;
        }
        lastAction.put(key, now);
        return true;
    }
}
