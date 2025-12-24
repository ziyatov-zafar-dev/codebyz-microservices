package uz.codebyz.message.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.codebyz.message.security.JwtUser;
import uz.codebyz.message.service.ChatDirectory;

import java.util.*;

@RestController
@RequestMapping("/api/messages/chats")
@Tag(name = "Private Chats", description = "REST helpers for private chats (in-memory). Use Swagger docs to see WebSocket commands.")
@SecurityRequirement(name = "bearerAuth")
public class ChatApiController {

    private final ChatDirectory chatDirectory;

    public ChatApiController(ChatDirectory chatDirectory) {
        this.chatDirectory = chatDirectory;
    }

    @PostMapping("/private")
    @Operation(summary = "Create or get private chat", description = "Creates an in-memory private chat between user1 and user2 if not exists; returns chatId.",
            responses = @ApiResponse(responseCode = "200", description = "Chat created or already exists",
                    content = @Content(schema = @Schema(implementation = ChatDirectory.ChatView.class))))
    public ResponseEntity<ChatDirectory.ChatView> createPrivate(@RequestBody CreateChatReq req,
                                                                @AuthenticationPrincipal JwtUser principal) {
        if (principal == null || req.user2Id == null) {
            return ResponseEntity.badRequest().build();
        }
        UUID chatId = req.chatId == null ? UUID.randomUUID() : req.chatId;
        // ensure uniqueness by participants: if exists, reuse
        var existing = chatDirectory.findByUsers(principal.getUserId(), req.user2Id());
        if (existing.isPresent()) {
            return ResponseEntity.ok(view(existing.get().getChatId(), principal.getUserId()));
        }
        chatDirectory.ensureChat(chatId, principal.getUserId(), req.user2Id);
        return ResponseEntity.ok(view(chatId, principal.getUserId()));
    }

    @GetMapping("/my")
    @Operation(summary = "List my chats", description = "Returns chat ids for the given userId.")
    public ResponseEntity<Set<UUID>> myChats(@AuthenticationPrincipal JwtUser principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(chatDirectory.chatIdsOf(principal.getUserId()));
    }

    @GetMapping("/{chatId}")
    @Operation(summary = "Get chat info", description = "Returns chat state for requester.")
    public ResponseEntity<ChatDirectory.ChatView> get(@PathVariable UUID chatId, @AuthenticationPrincipal JwtUser principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return chatDirectory.view(chatId, principal.getUserId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/exists")
    @Operation(summary = "Check chat exists between two users", description = "Returns boolean")
    public ResponseEntity<Map<String, Boolean>> exists(@RequestParam UUID user1, @RequestParam UUID user2) {
        boolean found = chatDirectory.findByUsers(user1, user2).isPresent();
        return ResponseEntity.ok(Map.of("exists", found));
    }

    @DeleteMapping("/{chatId}")
    @Operation(summary = "Delete chat", description = "Removes chat from memory.")
    public ResponseEntity<?> delete(@PathVariable UUID chatId, @AuthenticationPrincipal JwtUser principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        // only participants can delete
        return chatDirectory.find(chatId)
                .filter(c -> c.isParticipant(principal.getUserId()))
                .map(c -> chatDirectory.delete(chatId) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build())
                .orElse(ResponseEntity.status(403).build());
    }

    @DeleteMapping("/{chatId}/clear")
    @Operation(summary = "Clear chat events buffer", description = "Clears stored event buffer for replay.")
    public ResponseEntity<?> clear(@PathVariable UUID chatId, @AuthenticationPrincipal JwtUser principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return chatDirectory.find(chatId).map(c -> {
            if (!c.isParticipant(principal.getUserId())) return ResponseEntity.status(403).build();
            c.clearEvents();
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{chatId}/mute")
    @Operation(summary = "Mute chat", description = "Marks chat muted for requester.")
    public ResponseEntity<?> mute(@PathVariable UUID chatId, @AuthenticationPrincipal JwtUser principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return chatDirectory.find(chatId).map(c -> {
            if (!c.isParticipant(principal.getUserId())) return ResponseEntity.status(403).build();
            c.mute(principal.getUserId());
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{chatId}/unmute")
    @Operation(summary = "Unmute chat", description = "Unmutes chat for requester.")
    public ResponseEntity<?> unmute(@PathVariable UUID chatId, @AuthenticationPrincipal JwtUser principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return chatDirectory.find(chatId).map(c -> {
            if (!c.isParticipant(principal.getUserId())) return ResponseEntity.status(403).build();
            c.unmute(principal.getUserId());
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{chatId}/pin")
    @Operation(summary = "Pin chat", description = "Pins chat for requester.")
    public ResponseEntity<?> pin(@PathVariable UUID chatId, @AuthenticationPrincipal JwtUser principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return chatDirectory.find(chatId).map(c -> {
            if (!c.isParticipant(principal.getUserId())) return ResponseEntity.status(403).build();
            c.pin(principal.getUserId());
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{chatId}/unpin")
    @Operation(summary = "Unpin chat", description = "Unpins chat for requester.")
    public ResponseEntity<?> unpin(@PathVariable UUID chatId, @AuthenticationPrincipal JwtUser principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return chatDirectory.find(chatId).map(c -> {
            if (!c.isParticipant(principal.getUserId())) return ResponseEntity.status(403).build();
            c.unpin(principal.getUserId());
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    private ChatDirectory.ChatView view(UUID chatId, UUID requester) {
        return chatDirectory.view(chatId, requester)
                .orElse(new ChatDirectory.ChatView(chatId, null, null, null, false, false, 0));
    }

    public static class CreateChatReq {
        @Schema(description = "Optional chatId; generated if missing")
        public UUID chatId;
        @Schema(description = "First participant", required = true)
        public UUID user1Id;
        @Schema(description = "Second participant", required = true)
        public UUID user2Id;

        public UUID user2Id() {
            return user2Id;
        }
    }
}
