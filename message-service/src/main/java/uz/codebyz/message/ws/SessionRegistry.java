package uz.codebyz.message.ws;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import uz.codebyz.message.security.JwtUser;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionRegistry {

    private final Map<UUID, WebSocketSession> sessionsByUser = new ConcurrentHashMap<>();

    public void register(JwtUser user, WebSocketSession session) {
        sessionsByUser.put(user.getUserId(), session);
    }

    public void unregister(UUID userId) {
        sessionsByUser.remove(userId);
    }

    public WebSocketSession get(UUID userId) {
        return sessionsByUser.get(userId);
    }
}
