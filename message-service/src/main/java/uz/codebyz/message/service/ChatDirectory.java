package uz.codebyz.message.service;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Objects;

@Component
public class ChatDirectory {

    public static class ChatState {
        private final UUID chatId;
        private final UUID user1;
        private final UUID user2;
        private UUID blockedBy;
        private final AtomicLong seq = new AtomicLong(0);
        private final Deque<uz.codebyz.message.domain.EventEnvelope> events = new ArrayDeque<>();
        private final Set<UUID> mutedBy = new HashSet<>();
        private final Set<UUID> pinnedBy = new HashSet<>();

        public ChatState(UUID chatId, UUID user1, UUID user2) {
            this.chatId = chatId;
            this.user1 = user1;
            this.user2 = user2;
        }

        public UUID getChatId() {
            return chatId;
        }

        public UUID getUser1() {
            return user1;
        }

        public UUID getUser2() {
            return user2;
        }

        public boolean isParticipant(UUID userId) {
            return user1.equals(userId) || user2.equals(userId);
        }

        public UUID getPeer(UUID userId) {
            if (user1.equals(userId)) return user2;
            if (user2.equals(userId)) return user1;
            return null;
        }

        public boolean isBlocked() {
            return blockedBy != null;
        }

        public UUID getBlockedBy() {
            return blockedBy;
        }

        public void setBlockedBy(UUID blockedBy) {
            this.blockedBy = blockedBy;
        }

        public long nextSeq() {
            return seq.incrementAndGet();
        }

        public long lastSeq() {
            return seq.get();
        }

        public void addEvent(uz.codebyz.message.domain.EventEnvelope envelope) {
            events.addLast(envelope);
            while (events.size() > 200) {
                events.removeFirst();
            }
        }

        public List<uz.codebyz.message.domain.EventEnvelope> replayFrom(long lastSeq) {
            List<uz.codebyz.message.domain.EventEnvelope> replay = new ArrayList<>();
            for (uz.codebyz.message.domain.EventEnvelope env : events) {
                if (env.getEventSeq() > lastSeq) {
                    replay.add(env);
                }
            }
            return replay;
        }

        public void clearEvents() {
            events.clear();
        }

        public void mute(UUID userId) {
            mutedBy.add(userId);
        }

        public void unmute(UUID userId) {
            mutedBy.remove(userId);
        }

        public boolean isMutedBy(UUID userId) {
            return mutedBy.contains(userId);
        }

        public void pin(UUID userId) {
            pinnedBy.add(userId);
        }

        public void unpin(UUID userId) {
            pinnedBy.remove(userId);
        }

        public boolean isPinnedBy(UUID userId) {
            return pinnedBy.contains(userId);
        }
    }

    private final Map<UUID, ChatState> chats = new ConcurrentHashMap<>();
    private final Map<UUID, Set<UUID>> chatsByUser = new ConcurrentHashMap<>();

    public synchronized ChatState ensureChat(UUID chatId, UUID user1, UUID user2) {
        ChatState existing = chats.get(chatId);
        if (existing != null) {
            return existing;
        }
        ChatState created = new ChatState(chatId, user1, user2);
        chats.put(chatId, created);
        chatsByUser.computeIfAbsent(user1, k -> new HashSet<>()).add(chatId);
        chatsByUser.computeIfAbsent(user2, k -> new HashSet<>()).add(chatId);
        return created;
    }

    public Optional<ChatState> find(UUID chatId) {
        return Optional.ofNullable(chats.get(chatId));
    }

    public synchronized boolean delete(UUID chatId) {
        ChatState removed = chats.remove(chatId);
        if (removed == null) return false;
        chatsByUser.computeIfPresent(removed.getUser1(), (k, v) -> { v.remove(chatId); return v; });
        chatsByUser.computeIfPresent(removed.getUser2(), (k, v) -> { v.remove(chatId); return v; });
        return true;
    }

    public Set<UUID> chatIdsOf(UUID userId) {
        return chatsByUser.getOrDefault(userId, Set.of());
    }

    public Set<UUID> peersOf(UUID userId) {
        Set<UUID> peers = new HashSet<>();
        for (UUID chatId : chatIdsOf(userId)) {
            ChatState state = chats.get(chatId);
            if (state == null) continue;
            UUID peer = state.getPeer(userId);
            if (peer != null) {
                peers.add(peer);
            }
        }
        return peers;
    }

    public record ChatView(UUID chatId, UUID user1, UUID user2, UUID blockedBy,
                           boolean mutedByRequester, boolean pinnedByRequester, long lastSeq) {}

    public Optional<ChatView> view(UUID chatId, UUID requester) {
        return find(chatId).map(c -> new ChatView(
                c.getChatId(),
                c.getUser1(),
                c.getUser2(),
                c.getBlockedBy(),
                c.isMutedBy(requester),
                c.isPinnedBy(requester),
                c.lastSeq()
        ));
    }

    public Optional<ChatState> findByUsers(UUID user1, UUID user2) {
        return chatIdsOf(user1).stream()
                .map(chats::get)
                .filter(Objects::nonNull)
                .filter(c -> c.isParticipant(user2))
                .findFirst();
    }
}
