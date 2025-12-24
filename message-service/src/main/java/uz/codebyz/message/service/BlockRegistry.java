package uz.codebyz.message.service;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BlockRegistry {
    private final Set<UUID> blockedUsers = ConcurrentHashMap.newKeySet();
    private volatile boolean preventSend = false;

    public void block(UUID userId) {
        blockedUsers.add(userId);
    }

    public boolean unblock(UUID userId) {
        return blockedUsers.remove(userId);
    }

    public boolean isBlocked(UUID userId) {
        return blockedUsers.contains(userId);
    }

    public Set<UUID> list() {
        return Collections.unmodifiableSet(blockedUsers);
    }

    public void setPreventSend(boolean prevent) {
        this.preventSend = prevent;
    }

    public boolean isPreventSend() {
        return preventSend;
    }
}
