package uz.codebyz.message.service;

import org.springframework.stereotype.Service;
import uz.codebyz.message.domain.EventEnvelope;
import uz.codebyz.message.domain.EventType;
import uz.codebyz.message.dto.command.TypingCommand;
import uz.codebyz.message.dto.payload.TypingPayload;
import uz.codebyz.message.security.JwtUser;
import uz.codebyz.message.ws.EventBroadcaster;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class TypingService {

    private final ChatDirectory chatDirectory;
    private final EventBroadcaster broadcaster;
    private final RateLimitService rateLimitService;

    public TypingService(ChatDirectory chatDirectory, EventBroadcaster broadcaster, RateLimitService rateLimitService) {
        this.chatDirectory = chatDirectory;
        this.broadcaster = broadcaster;
        this.rateLimitService = rateLimitService;
    }

    public void typing(JwtUser user, TypingCommand cmd) {
        chatDirectory.find(cmd.getChatId()).ifPresent(chat -> {
            if (!chat.isParticipant(user.getUserId())) {
                return;
            }
            if (!rateLimitService.allow(user.getUserId(), chat.getChatId(), "typing", Duration.ofSeconds(2))) {
                return;
            }
            long seq = chat.nextSeq();
            TypingPayload payload = new TypingPayload();
            payload.setChatId(chat.getChatId());
            payload.setUserId(user.getUserId());
            EventEnvelope env = new EventEnvelope(
                    UUID.randomUUID(),
                    cmd.isTyping() ? EventType.USER_TYPING_START : EventType.USER_TYPING_STOP,
                    chat.getChatId(),
                    seq,
                    user.getUserId(),
                    Instant.now(),
                    null,
                    payload
            );
            broadcaster.broadcastToChat(env);
        });
    }
}
