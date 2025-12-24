package uz.codebyz.message.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.codebyz.message.service.PresenceService;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/ws/messages")
@Tag(name = "WebSocket Info", description = "REST helpers describing WebSocket connect/send/receive and presence status.")
public class WsInfoController {

    private final PresenceService presenceService;

    public WsInfoController(PresenceService presenceService) {
        this.presenceService = presenceService;
    }

    @GetMapping("/connect")
    @Operation(summary = "WebSocket connect info", description = "Returns endpoint and headers required to open WS connection.")
    public ResponseEntity<Map<String, String>> connectInfo() {
        return ResponseEntity.ok(Map.of(
                "endpoint", "/ws",
                "authHeader", "Authorization: Bearer <JWT>",
                "protocol", "JSON envelopes: {\"type\": \"MESSAGE_SEND\", \"payload\": { ... }}"));
    }

    @GetMapping("/send")
    @Operation(summary = "WebSocket send format", description = "Describes how to send messages over WebSocket.")
    public ResponseEntity<Map<String, String>> sendInfo() {
        return ResponseEntity.ok(Map.of(
                "envelope", "{\"type\":\"MESSAGE_SEND\",\"payload\":{...}}",
                "payloadFields", "chatId, peerId, content/type/file, replyToMessageId, clientEventId, tempMessageId"));
    }

    @GetMapping("/receive")
    @Operation(summary = "WebSocket receive format", description = "Describes events received over WebSocket.")
    public ResponseEntity<Map<String, String>> receiveInfo() {
        return ResponseEntity.ok(Map.of(
                "eventEnvelope", "eventId, eventType, chatId, eventSeq, actorUserId, timestamp, clientEventId, payload",
                "events", "MESSAGE_CREATED, MESSAGE_EDITED, MESSAGE_DELETED, MESSAGE_READ, SYSTEM_MESSAGE_CREATED, USER_TYPING_START/STOP, USER_ONLINE/OFFLINE, CHAT_BLOCKED/UNBLOCKED"));
    }

    @GetMapping("/status/online")
    @Operation(summary = "Check user online", description = "Returns online status for userId (in-memory).")
    public ResponseEntity<Map<String, Boolean>> online(@RequestParam UUID userId) {
        return ResponseEntity.ok(Map.of("online", presenceService.isOnline(userId)));
    }

    @GetMapping("/status/last-seen")
    @Operation(summary = "Last seen", description = "Returns last seen timestamp for userId (in-memory).")
    public ResponseEntity<Map<String, String>> lastSeen(@RequestParam UUID userId) {
        Instant seen = presenceService.getLastSeen(userId);
        return ResponseEntity.ok(Map.of("lastSeen", seen == null ? null : seen.toString()));
    }
}
