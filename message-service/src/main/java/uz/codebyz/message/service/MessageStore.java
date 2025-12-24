package uz.codebyz.message.service;

import org.springframework.stereotype.Component;
import uz.codebyz.message.domain.MessageType;
import uz.codebyz.message.dto.payload.MessagePayload;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MessageStore {

    private final Map<String, MessagePayload> messages = new ConcurrentHashMap<>();
    private final Map<UUID, List<MessagePayload>> byChat = new ConcurrentHashMap<>();

    public MessagePayload create(UUID chatId, UUID senderId, String content, MessageType type,
                                 MessagePayload.FileMeta file, String replyTo, boolean system,
                                 String forwardFrom) {
        MessagePayload payload = new MessagePayload();
        payload.setMessageId(UUID.randomUUID());
        payload.setChatId(chatId);
        payload.setSenderId(senderId);
        payload.setContent(content);
        payload.setType(type);
        payload.setFile(file);
        payload.setReplyToMessageId(replyTo);
        payload.setForwardFromMessageId(forwardFrom);
        payload.setCreatedAt(Instant.now());
        payload.setEdited(false);
        payload.setStatus("SENT");
        payload.setSystem(system);
        messages.put(payload.getMessageId().toString(), payload);
        byChat.computeIfAbsent(chatId, k -> new ArrayList<>()).add(payload);
        return payload;
    }

    public Optional<MessagePayload> find(String messageId) {
        return Optional.ofNullable(messages.get(messageId));
    }

    public List<MessagePayload> list(UUID chatId) {
        return byChat.getOrDefault(chatId, List.of());
    }

    public Optional<MessagePayload> edit(String messageId, String content) {
        return find(messageId).map(msg -> {
            msg.setContent(content);
            msg.setEdited(true);
            msg.setEditedAt(Instant.now());
            return msg;
        });
    }

    public boolean deleteForAll(String messageId) {
        MessagePayload payload = messages.remove(messageId);
        if (payload == null) return false;
        payload.setDeletedForAll(true);
        List<MessagePayload> list = byChat.getOrDefault(payload.getChatId(), Collections.emptyList());
        list.removeIf(m -> m.getMessageId().toString().equals(messageId));
        return true;
    }

    public boolean deleteForUser(String messageId, UUID userId) {
        return find(messageId).map(msg -> msg.getDeletedForUsers().add(userId)).orElse(false);
    }

    public Optional<MessagePayload> lastMessage(UUID chatId) {
        List<MessagePayload> list = byChat.getOrDefault(chatId, List.of());
        if (list.isEmpty()) return Optional.empty();
        return Optional.of(list.get(list.size() - 1));
    }

    public List<MessagePayload> search(UUID chatId, String q) {
        String query = q == null ? "" : q.toLowerCase();
        List<MessagePayload> result = new ArrayList<>();
        for (MessagePayload msg : byChat.getOrDefault(chatId, List.of())) {
            if (msg.getContent() != null && msg.getContent().toLowerCase().contains(query)) {
                result.add(msg);
            }
        }
        return result;
    }

    public boolean markDelivered(String messageId, UUID userId) {
        return find(messageId).map(msg -> msg.getDeliveredTo().add(userId)).orElse(false);
    }

    public boolean markRead(String messageId, UUID userId) {
        return find(messageId).map(msg -> msg.getReadBy().add(userId)).orElse(false);
    }

    public long unreadCount(UUID userId) {
        return messages.values().stream()
                .filter(m -> !m.isDeletedForAll())
                .filter(m -> !m.getDeletedForUsers().contains(userId))
                .filter(m -> !m.getReadBy().contains(userId))
                .count();
    }

    public Map<UUID, Long> unreadByChat(UUID userId) {
        Map<UUID, Long> map = new HashMap<>();
        byChat.forEach((chatId, list) -> {
            long cnt = list.stream()
                    .filter(m -> !m.isDeletedForAll())
                    .filter(m -> !m.getDeletedForUsers().contains(userId))
                    .filter(m -> !m.getReadBy().contains(userId))
                    .count();
            map.put(chatId, cnt);
        });
        return map;
    }
}
