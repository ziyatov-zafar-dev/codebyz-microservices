package uz.codebyz.message.service;

import org.springframework.stereotype.Service;
import uz.codebyz.message.domain.EventEnvelope;
import uz.codebyz.message.domain.EventType;
import uz.codebyz.message.domain.MessageType;
import uz.codebyz.message.domain.SystemAction;
import uz.codebyz.message.dto.command.MessageDeleteCommand;
import uz.codebyz.message.dto.command.MessageEditCommand;
import uz.codebyz.message.dto.command.MessageReadCommand;
import uz.codebyz.message.dto.command.MessageSendCommand;
import uz.codebyz.message.dto.command.ReactionCommand;
import uz.codebyz.message.dto.payload.MessagePayload;
import uz.codebyz.message.dto.payload.ReactionPayload;
import uz.codebyz.message.dto.payload.ErrorPayload;
import uz.codebyz.message.security.JwtUser;
import uz.codebyz.message.ws.EventBroadcaster;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class MessagingService {

    private final ChatDirectory chatDirectory;
    private final EventBroadcaster broadcaster;
    private final RateLimitService rateLimitService;
    private final SystemMessageService systemMessageService;

    public MessagingService(ChatDirectory chatDirectory, EventBroadcaster broadcaster, RateLimitService rateLimitService, SystemMessageService systemMessageService) {
        this.chatDirectory = chatDirectory;
        this.broadcaster = broadcaster;
        this.rateLimitService = rateLimitService;
        this.systemMessageService = systemMessageService;
    }

    public void send(JwtUser user, MessageSendCommand cmd) {
        if (cmd.getChatId() == null || cmd.getPeerId() == null) {
            emitError(cmd.getChatId(), user.getUserId(), "VALIDATION_ERROR", "chatId and peerId are required");
            return;
        }
        if (cmd.getContent() == null || cmd.getContent().isBlank()) {
            emitError(cmd.getChatId(), user.getUserId(), "VALIDATION_ERROR", "content is required");
            return;
        }
        var existing = chatDirectory.find(cmd.getChatId());
        boolean isNewChat = existing.isEmpty();
        ChatDirectory.ChatState chat = existing
                .orElseGet(() -> chatDirectory.ensureChat(cmd.getChatId(), user.getUserId(), cmd.getPeerId()));
        if (!chat.isParticipant(cmd.getPeerId())) {
            emitError(chat.getChatId(), user.getUserId(), "VALIDATION_ERROR", "peerId must be a participant");
            return;
        }

        // Prevent send if globally blocked or sender blocked (reuse REST block registry via exception handled upstream)
        if (!chat.isParticipant(user.getUserId())) {
            emitError(chat.getChatId(), user.getUserId(), "ACCESS_DENIED", "User not in chat");
            return;
        }
        if (chat.isBlocked() && !user.getUserId().equals(chat.getBlockedBy())) {
            emitError(chat.getChatId(), user.getUserId(), "BLOCKED", "Chat is blocked");
            return;
        }
        if (!rateLimitService.allow(user.getUserId(), chat.getChatId(), "message", Duration.ofMillis(500))) {
            emitError(chat.getChatId(), user.getUserId(), "RATE_LIMIT", "Too many messages");
            return;
        }

        UUID messageId = UUID.randomUUID();
        long seq = chat.nextSeq();
        MessagePayload payload = new MessagePayload();
        payload.setMessageId(messageId);
        payload.setTempMessageId(cmd.getTempMessageId());
        payload.setSenderId(user.getUserId());
        payload.setChatId(chat.getChatId());
        payload.setContent(cmd.getContent().trim());
        payload.setType(cmd.getType() == null ? MessageType.TEXT : cmd.getType());
        if (cmd.getFile() != null) {
            MessagePayload.FileMeta fm = new MessagePayload.FileMeta();
            fm.setName(cmd.getFile().getName());
            fm.setUrl(cmd.getFile().getUrl());
            fm.setSize(cmd.getFile().getSize());
            payload.setFile(fm);
        }
        payload.setReplyToMessageId(cmd.getReplyToMessageId());
        payload.setReactions(cmd.getReactions());
        payload.setCreatedAt(Instant.now());
        payload.setEdited(false);
        payload.setStatus("SENT");

        EventEnvelope envelope = new EventEnvelope(
                UUID.randomUUID(),
                EventType.MESSAGE_CREATED,
                chat.getChatId(),
                seq,
                user.getUserId(),
                Instant.now(),
                cmd.getClientEventId(),
                payload
        );
        broadcaster.broadcastToChat(envelope);
        if (isNewChat) {
            systemMessageService.systemForAction(chat, SystemAction.CHAT_CREATED);
        }
    }

    public void edit(JwtUser user, MessageEditCommand cmd) {
        if (cmd.getChatId() == null || cmd.getMessageId() == null) {
            emitError(cmd.getChatId(), user.getUserId(), "VALIDATION_ERROR", "chatId and messageId are required");
            return;
        }
        chatDirectory.find(cmd.getChatId()).ifPresent(chat -> {
            if (!chat.isParticipant(user.getUserId())) {
                emitError(chat.getChatId(), user.getUserId(), "ACCESS_DENIED", "User not in chat");
                return;
            }
            if (cmd.getContent() == null || cmd.getContent().isBlank()) {
                emitError(chat.getChatId(), user.getUserId(), "VALIDATION_ERROR", "content is required");
                return;
            }
            long seq = chat.nextSeq();
            MessagePayload payload = new MessagePayload();
            payload.setMessageId(UUID.fromString(cmd.getMessageId()));
            payload.setChatId(chat.getChatId());
            payload.setContent(cmd.getContent());
            payload.setEdited(true);
            payload.setEditedAt(Instant.now());

            EventEnvelope envelope = new EventEnvelope(
                    UUID.randomUUID(),
                    EventType.MESSAGE_EDITED,
                    chat.getChatId(),
                    seq,
                    user.getUserId(),
                    Instant.now(),
                    cmd.getClientEventId(),
                    payload
            );
            broadcaster.broadcastToChat(envelope);
            systemMessageService.systemForAction(chat, SystemAction.MESSAGE_EDITED);
        });
    }

    public void delete(JwtUser user, MessageDeleteCommand cmd) {
        if (cmd.getChatId() == null || cmd.getMessageId() == null) {
            emitError(cmd.getChatId(), user.getUserId(), "VALIDATION_ERROR", "chatId and messageId are required");
            return;
        }
        chatDirectory.find(cmd.getChatId()).ifPresent(chat -> {
            if (!chat.isParticipant(user.getUserId())) {
                emitError(chat.getChatId(), user.getUserId(), "ACCESS_DENIED", "User not in chat");
                return;
            }
            long seq = chat.nextSeq();
            MessagePayload payload = new MessagePayload();
            payload.setMessageId(UUID.fromString(cmd.getMessageId()));
            payload.setChatId(chat.getChatId());

            EventEnvelope envelope = new EventEnvelope(
                    UUID.randomUUID(),
                    EventType.MESSAGE_DELETED,
                    chat.getChatId(),
                    seq,
                    user.getUserId(),
                    Instant.now(),
                    cmd.getClientEventId(),
                    payload
            );
            broadcaster.broadcastToChat(envelope);
            systemMessageService.systemForAction(chat, SystemAction.MESSAGE_DELETED);
        });
    }

    public void read(JwtUser user, MessageReadCommand cmd) {
        if (cmd.getChatId() == null || cmd.getMessageId() == null) {
            emitError(cmd.getChatId(), user.getUserId(), "VALIDATION_ERROR", "chatId and messageId are required");
            return;
        }
        chatDirectory.find(cmd.getChatId()).ifPresent(chat -> {
            if (!chat.isParticipant(user.getUserId())) {
                emitError(chat.getChatId(), user.getUserId(), "ACCESS_DENIED", "User not in chat");
                return;
            }
            long seq = chat.nextSeq();
            MessagePayload payload = new MessagePayload();
            payload.setMessageId(UUID.fromString(cmd.getMessageId()));
            payload.setChatId(chat.getChatId());
            payload.setStatus("READ");
            payload.getReadBy().add(user.getUserId());

            EventEnvelope envelope = new EventEnvelope(
                    UUID.randomUUID(),
                    EventType.MESSAGE_READ,
                    chat.getChatId(),
                    seq,
                    user.getUserId(),
                    Instant.now(),
                    cmd.getClientEventId(),
                    payload
            );
            broadcaster.broadcastToChat(envelope);
        });
    }

    public void react(JwtUser user, ReactionCommand cmd) {
        if (cmd.getChatId() == null || cmd.getMessageId() == null || cmd.getEmoji() == null) {
            emitError(cmd.getChatId(), user.getUserId(), "VALIDATION_ERROR", "chatId, messageId and emoji are required");
            return;
        }
        chatDirectory.find(cmd.getChatId()).ifPresent(chat -> {
            if (!chat.isParticipant(user.getUserId())) {
                emitError(chat.getChatId(), user.getUserId(), "ACCESS_DENIED", "User not in chat");
                return;
            }
            if (!rateLimitService.allow(user.getUserId(), chat.getChatId(), "reaction", Duration.ofMillis(300))) {
                emitError(chat.getChatId(), user.getUserId(), "RATE_LIMIT", "Too many reactions");
                return;
            }
            long seq = chat.nextSeq();
            ReactionPayload payload = new ReactionPayload();
            payload.setChatId(chat.getChatId());
            payload.setMessageId(UUID.fromString(cmd.getMessageId()));
            payload.setEmoji(cmd.getEmoji());
            payload.setUserId(user.getUserId());

            EventEnvelope envelope = new EventEnvelope(
                    UUID.randomUUID(),
                    cmd.isAdd() ? EventType.MESSAGE_REACTION_ADDED : EventType.MESSAGE_REACTION_REMOVED,
                    chat.getChatId(),
                    seq,
                    user.getUserId(),
                    Instant.now(),
                    cmd.getClientEventId(),
                    payload
            );
            broadcaster.broadcastToChat(envelope);
        });
    }

    private void emitError(UUID chatId, UUID userId, String code, String message) {
        EventEnvelope env = new EventEnvelope(
                UUID.randomUUID(),
                EventType.ERROR,
                chatId,
                0,
                null,
                Instant.now(),
                null,
                new ErrorPayload(code, message)
        );
        broadcaster.sendToUser(env, userId);
    }
}
