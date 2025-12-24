package uz.codebyz.message.ws;

import com.google.gson.Gson;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import uz.codebyz.message.domain.EventEnvelope;
import uz.codebyz.message.service.ChatDirectory;

import java.io.IOException;
import java.util.UUID;

@Component
public class EventBroadcaster {

    private final SessionRegistry sessionRegistry;
    private final ChatDirectory chatDirectory;
    private final Gson gson = new Gson();

    public EventBroadcaster(SessionRegistry sessionRegistry, ChatDirectory chatDirectory) {
        this.sessionRegistry = sessionRegistry;
        this.chatDirectory = chatDirectory;
    }

    public void broadcastToChat(EventEnvelope envelope) {
        chatDirectory.find(envelope.getChatId()).ifPresent(chat -> {
            send(envelope, chat.getUser1());
            send(envelope, chat.getUser2());
            chat.addEvent(envelope);
        });
    }

    public void sendToUser(EventEnvelope envelope, UUID userId) {
        send(envelope, userId);
    }

    private void send(EventEnvelope envelope, UUID userId) {
        WebSocketSession session = sessionRegistry.get(userId);
        if (session == null || !session.isOpen()) {
            return;
        }
        try {
            session.sendMessage(new TextMessage(gson.toJson(envelope)));
        } catch (IOException ignored) {
            // ignore for now
        }
    }
}
