package uz.codebyz.message.ws;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import uz.codebyz.message.dto.command.*;
import uz.codebyz.message.security.JwtUser;
import uz.codebyz.message.service.BlockService;
import uz.codebyz.message.service.MessagingService;
import uz.codebyz.message.service.PresenceService;
import uz.codebyz.message.service.ReplayService;
import uz.codebyz.message.service.TypingService;

@Component
public class MessagingWebSocketHandler extends TextWebSocketHandler {

    private final SessionRegistry sessionRegistry;
    private final MessagingService messagingService;
    private final TypingService typingService;
    private final BlockService blockService;
    private final PresenceService presenceService;
    private final ReplayService replayService;
    private final Gson gson = new Gson();

    public MessagingWebSocketHandler(SessionRegistry sessionRegistry,
                                     MessagingService messagingService,
                                     TypingService typingService,
                                     BlockService blockService,
                                     PresenceService presenceService,
                                     ReplayService replayService) {
        this.sessionRegistry = sessionRegistry;
        this.messagingService = messagingService;
        this.typingService = typingService;
        this.blockService = blockService;
        this.presenceService = presenceService;
        this.replayService = replayService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        JwtUser user = (JwtUser) session.getAttributes().get("user");
        if (user == null) {
            try {
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Unauthorized"));
            } catch (Exception ignored) {
            }
            return;
        }
        sessionRegistry.register(user, session);
        presenceService.online(user);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        JwtUser user = (JwtUser) session.getAttributes().get("user");
        JsonObject obj = gson.fromJson(message.getPayload(), JsonObject.class);
        if (obj == null || !obj.has("type")) {
            sendError(session, "INVALID", "Missing type");
            return;
        }
        String type = obj.get("type").getAsString();
        JsonObject payload = obj.has("payload") ? obj.getAsJsonObject("payload") : new JsonObject();

        try {
            switch (type) {
                case "MESSAGE_SEND" -> messagingService.send(user, gson.fromJson(payload, MessageSendCommand.class));
                case "MESSAGE_EDIT" -> messagingService.edit(user, gson.fromJson(payload, MessageEditCommand.class));
                case "MESSAGE_DELETE" -> messagingService.delete(user, gson.fromJson(payload, MessageDeleteCommand.class));
                case "MESSAGE_READ" -> messagingService.read(user, gson.fromJson(payload, MessageReadCommand.class));
                case "REACTION" -> messagingService.react(user, gson.fromJson(payload, ReactionCommand.class));
                case "TYPING" -> typingService.typing(user, gson.fromJson(payload, TypingCommand.class));
                case "BLOCK" -> blockService.handle(user, gson.fromJson(payload, BlockCommand.class));
                case "EVENT_ACK" -> replayService.replay(user, gson.fromJson(payload, EventAckCommand.class));
                default -> sendError(session, "UNKNOWN_TYPE", "Unsupported event type");
            }
        } catch (Exception e) {
            sendError(session, "PROCESSING_ERROR", e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        JwtUser user = (JwtUser) session.getAttributes().get("user");
        if (user != null) {
            sessionRegistry.unregister(user.getUserId());
            presenceService.offline(user);
        }
    }

    private void sendError(WebSocketSession session, String code, String message) {
        try {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "ERROR");
            JsonObject payload = new JsonObject();
            payload.addProperty("code", code);
            payload.addProperty("message", message);
            obj.add("payload", payload);
            session.sendMessage(new TextMessage(gson.toJson(obj)));
        } catch (Exception ignored) {
        }
    }
}
