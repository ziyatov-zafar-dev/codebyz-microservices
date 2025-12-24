package uz.codebyz.message.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.codebyz.message.domain.MessageType;
import uz.codebyz.message.dto.payload.MessagePayload;
import uz.codebyz.message.service.MessageStore;
import uz.codebyz.message.service.BlockRegistry;
import uz.codebyz.message.service.TypingRegistry;
import uz.codebyz.message.storage.FileStorageService;
import uz.codebyz.message.service.AuthUserClient;

import java.util.List;
import java.util.UUID;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "Messages", description = "REST APIs for sending and managing messages (in-memory demo).")
@SecurityRequirement(name = "bearerAuth")
public class MessageApiController {

    private final MessageStore messageStore;
    private final BlockRegistry blockRegistry;
    private final TypingRegistry typingRegistry;
    private final FileStorageService fileStorageService;
    private final AuthUserClient authUserClient;

    public MessageApiController(MessageStore messageStore, BlockRegistry blockRegistry, TypingRegistry typingRegistry, FileStorageService fileStorageService, AuthUserClient authUserClient) {
        this.messageStore = messageStore;
        this.blockRegistry = blockRegistry;
        this.typingRegistry = typingRegistry;
        this.fileStorageService = fileStorageService;
        this.authUserClient = authUserClient;
    }

    @PostMapping("/{messageId}/delivered")
    @Operation(summary = "Mark delivered", description = "Marks message as delivered for a user.")
    public ResponseEntity<Void> delivered(@PathVariable String messageId, @RequestParam UUID userId) {
        return messageStore.markDelivered(messageId, userId) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/{messageId}/read")
    @Operation(summary = "Mark read", description = "Marks message as read for a user.")
    public ResponseEntity<Void> read(@PathVariable String messageId, @RequestParam UUID userId) {
        return messageStore.markRead(messageId, userId) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/unread/count")
    @Operation(summary = "Unread count", description = "Returns total unread for user.")
    public ResponseEntity<Long> unreadCount(@RequestParam UUID userId) {
        return ResponseEntity.ok(messageStore.unreadCount(userId));
    }

    @GetMapping("/unread/count-by-chat")
    @Operation(summary = "Unread count by chat", description = "Returns unread counts grouped by chat for user.")
    public ResponseEntity<java.util.Map<UUID, Long>> unreadByChat(@RequestParam UUID userId) {
        return ResponseEntity.ok(messageStore.unreadByChat(userId));
    }

    @PostMapping("/chat/{chatId}/read-all")
    @Operation(summary = "Mark chat as read", description = "Marks all messages in chat as read for user.")
    public ResponseEntity<Void> readAll(@PathVariable UUID chatId, @RequestParam UUID userId) {
        messageStore.list(chatId).forEach(m -> m.getReadBy().add(userId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{messageId}/read-by")
    @Operation(summary = "Who read message", description = "Returns list of userIds who read the message.")
    public ResponseEntity<java.util.Set<UUID>> readBy(@PathVariable String messageId) {
        return messageStore.find(messageId)
                .map(m -> ResponseEntity.ok(m.getReadBy()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/typing/start")
    @Operation(summary = "Typing start (REST)", description = "Helper endpoint to signal typing start.")
    public ResponseEntity<java.util.Map<String, Object>> typingStart(@RequestParam("chatId") UUID chatId,
                                                                      @RequestParam UUID userId) {
        typingRegistry.start(chatId, userId);
        return ResponseEntity.ok(java.util.Map.of("chatId", chatId, "typingUsers", typingRegistry.who(chatId)));
    }

    @PostMapping("/typing/stop")
    @Operation(summary = "Typing stop (REST)", description = "Helper endpoint to signal typing stop.")
    public ResponseEntity<java.util.Map<String, Object>> typingStop(@RequestParam UUID chatId,
                                                                     @RequestParam UUID userId) {
        typingRegistry.stop(chatId, userId);
        return ResponseEntity.ok(java.util.Map.of("chatId", chatId, "typingUsers", typingRegistry.who(chatId)));
    }

    @PostMapping("/block/{userId}")
    @Operation(summary = "Block user", description = "Blocks a user globally (in-memory).")
    public ResponseEntity<Void> block(@PathVariable UUID userId) {
        blockRegistry.block(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/unblock/{userId}")
    @Operation(summary = "Unblock user", description = "Unblocks a user globally.")
    public ResponseEntity<Void> unblock(@PathVariable UUID userId) {
        blockRegistry.unblock(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/block/check/{userId}")
    @Operation(summary = "Check block status", description = "Returns whether the user is blocked.")
    public ResponseEntity<java.util.Map<String, Boolean>> checkBlock(@PathVariable UUID userId) {
        return ResponseEntity.ok(java.util.Map.of("blocked", blockRegistry.isBlocked(userId)));
    }

    @GetMapping("/block/list")
    @Operation(summary = "List blocked users")
    public ResponseEntity<java.util.Set<UUID>> listBlocked() {
        return ResponseEntity.ok(blockRegistry.list());
    }

    @PostMapping("/block/prevent-send")
    @Operation(summary = "Toggle prevent-send", description = "Sets global prevent-send flag (true=block all sends).")
    public ResponseEntity<java.util.Map<String, Boolean>> preventSend(@RequestParam boolean prevent) {
        blockRegistry.setPreventSend(prevent);
        return ResponseEntity.ok(java.util.Map.of("preventSend", blockRegistry.isPreventSend()));
    }

    @PostMapping("/send/text")
    @Operation(summary = "Send text message", responses = @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = MessagePayload.class))))
    public ResponseEntity<MessagePayload> sendText( @RequestBody SendRequest req) {
        return sendMessage(req, MessageType.TEXT, false, null, null);
    }

    @PostMapping("/send/system")
    @Operation(summary = "Send system message", description = "System message (no sender required).")
    public ResponseEntity<MessagePayload> sendSystem(@RequestBody SendRequest req) {
        return sendMessage(req, MessageType.TEXT, true, null, null);
    }

    @PostMapping("/system/user-joined")
    @Operation(summary = "System: user joined")
    public ResponseEntity<MessagePayload> systemUserJoined(@RequestBody SendRequest req) {
        req.content = "[SYSTEM] User joined chat";
        return sendSystem(req);
    }

    @PostMapping("/system/user-left")
    @Operation(summary = "System: user left")
    public ResponseEntity<MessagePayload> systemUserLeft(@RequestBody SendRequest req) {
        req.content = "[SYSTEM] User left chat";
        return sendSystem(req);
    }

    @PostMapping("/system/chat-created")
    @Operation(summary = "System: chat created")
    public ResponseEntity<MessagePayload> systemChatCreated(@RequestBody SendRequest req) {
        req.content = "[SYSTEM] Chat created";
        return sendSystem(req);
    }

    @PostMapping("/system/chat-cleared")
    @Operation(summary = "System: chat cleared")
    public ResponseEntity<MessagePayload> systemChatCleared(@RequestBody SendRequest req) {
        req.content = "[SYSTEM] Chat cleared";
        return sendSystem(req);
    }

    @PostMapping("/system/message-deleted")
    @Operation(summary = "System: message deleted")
    public ResponseEntity<MessagePayload> systemMessageDeleted(@RequestBody SendRequest req) {
        req.content = "[SYSTEM] Message deleted";
        return sendSystem(req);
    }

    @PostMapping("/system/user-blocked")
    @Operation(summary = "System: user blocked")
    public ResponseEntity<MessagePayload> systemUserBlocked(@RequestBody SendRequest req) {
        req.content = "[SYSTEM] User blocked";
        return sendSystem(req);
    }

    @PostMapping("/system/user-unblocked")
    @Operation(summary = "System: user unblocked")
    public ResponseEntity<MessagePayload> systemUserUnblocked(@RequestBody SendRequest req) {
        req.content = "[SYSTEM] User unblocked";
        return sendSystem(req);
    }

    @PostMapping("/send/image")
    @Operation(summary = "Send image message")
    public ResponseEntity<MessagePayload> sendImage(@RequestBody SendRequest req) {
        return sendMessage(req, MessageType.IMAGE, false, null, null);
    }

    @PostMapping("/send/photo")
    @Operation(summary = "Send photo message", description = "Alias for image message")
    public ResponseEntity<MessagePayload> sendPhoto(@RequestBody SendRequest req) {
        return sendMessage(req, MessageType.IMAGE, false, null, null);
    }

    @PostMapping(value = "/send/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Send image (upload)")
    public ResponseEntity<MessagePayload> sendImageUpload(@RequestParam("chatId") UUID chatId,
                                                          @RequestParam("senderId") UUID senderId,
                                                          @RequestPart("file") MultipartFile file,
                                                          @RequestParam(name = "caption", required = false) String caption,
                                                          HttpServletRequest request) {
        FileReq fr = storeFile("image", file, request);
        SendRequest sr = new SendRequest();
        sr.chatId = chatId;
        sr.senderId = senderId;
        sr.content = caption;
        sr.file = fr;
        return sendMessage(sr, MessageType.IMAGE, false, null, null);
    }

    @PostMapping(value = "/send/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Send photo (upload)")
    public ResponseEntity<MessagePayload> sendPhotoUpload(@RequestParam("chatId") UUID chatId,
                                                          @RequestParam("senderId") UUID senderId,
                                                          @RequestPart("file") MultipartFile file,
                                                          @RequestParam(name = "caption", required = false) String caption,
                                                          HttpServletRequest request) {
        FileReq fr = storeFile("photo", file, request);
        SendRequest sr = new SendRequest();
        sr.chatId = chatId;
        sr.senderId = senderId;
        sr.content = caption;
        sr.file = fr;
        return sendMessage(sr, MessageType.IMAGE, false, null, null);
    }

    @PostMapping("/send/file")
    @Operation(summary = "Send file message")
    public ResponseEntity<MessagePayload> sendFile(@RequestBody SendRequest req) {
        return sendMessage(req, MessageType.FILE, false, null, null);
    }

    @PostMapping(value = "/send/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Send file (upload)")
    public ResponseEntity<MessagePayload> sendFileUpload(@RequestParam("chatId") UUID chatId,
                                                         @RequestParam("senderId") UUID senderId,
                                                         @RequestPart("file") MultipartFile file,
                                                         @RequestParam(name = "caption", required = false) String caption,
                                                         HttpServletRequest request) {
        FileReq fr = storeFile("file", file, request);
        SendRequest sr = new SendRequest();
        sr.chatId = chatId;
        sr.senderId = senderId;
        sr.content = caption;
        sr.file = fr;
        return sendMessage(sr, MessageType.FILE, false, null, null);
    }

    @PostMapping("/send/voice")
    @Operation(summary = "Send voice message")
    public ResponseEntity<MessagePayload> sendVoice(@RequestBody SendRequest req) {
        return sendMessage(req, MessageType.VOICE, false, null, null);
    }

    @PostMapping(value = "/send/voice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Send voice (upload)")
    public ResponseEntity<MessagePayload> sendVoiceUpload(@RequestParam("chatId") UUID chatId,
                                                          @RequestParam("senderId") UUID senderId,
                                                          @RequestPart("file") MultipartFile file,
                                                          @RequestParam(name = "caption", required = false) String caption,
                                                          HttpServletRequest request) {
        FileReq fr = storeFile("voice", file, request);
        SendRequest sr = new SendRequest();
        sr.chatId = chatId;
        sr.senderId = senderId;
        sr.content = caption;
        sr.file = fr;
        return sendMessage(sr, MessageType.VOICE, false, null, null);
    }

    @PostMapping("/send/video")
    @Operation(summary = "Send video message")
    public ResponseEntity<MessagePayload> sendVideo(@RequestBody SendRequest req) {
        return sendMessage(req, MessageType.VIDEO, false, null, null);
    }

    @PostMapping(value = "/send/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Send video (upload)")
    public ResponseEntity<MessagePayload> sendVideoUpload(@RequestParam("chatId") UUID chatId,
                                                          @RequestParam("senderId") UUID senderId,
                                                          @RequestPart("file") MultipartFile file,
                                                          @RequestParam(name = "caption", required = false) String caption,
                                                          HttpServletRequest request) {
        FileReq fr = storeFile("video", file, request);
        SendRequest sr = new SendRequest();
        sr.chatId = chatId;
        sr.senderId = senderId;
        sr.content = caption;
        sr.file = fr;
        return sendMessage(sr, MessageType.VIDEO, false, null, null);
    }

    @PostMapping("/{messageId}/reply")
    @Operation(summary = "Reply to message")
    public ResponseEntity<MessagePayload> reply(@PathVariable String messageId, @RequestBody SendRequest req) {
        return sendMessage(req, MessageType.TEXT, false, messageId, null);
    }

    @PostMapping("/{messageId}/forward")
    @Operation(summary = "Forward message")
    public ResponseEntity<MessagePayload> forward(@PathVariable("messageId") String messageId, @RequestBody ForwardRequest req) {
        MessagePayload original = messageStore.find(messageId).orElse(null);
        if (original == null) return ResponseEntity.notFound().build();
        SendRequest send = new SendRequest();
        send.chatId = req.chatId;
        send.senderId = req.senderId;
        send.content = original.getContent();
        if (original.getFile() != null) {
            FileReq fr = new FileReq();
            fr.name = original.getFile().getName();
            fr.url = original.getFile().getUrl();
            fr.size = original.getFile().getSize();
            fr.mime = original.getFile().getSizeReadable(); // reuse as mime if stored there
            send.file = fr;
        }
        return sendMessage(send, original.getType(), false, null, messageId);
    }

    @PutMapping("/{messageId}/edit")
    @Operation(summary = "Edit message")
    public ResponseEntity<MessagePayload> edit(@PathVariable("messageId") String messageId, @RequestBody EditRequest req) {
        return messageStore.edit(messageId, req.content)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{messageId}/delete-for-me")
    @Operation(summary = "Delete message for current user")
    public ResponseEntity<Void> deleteForMe(@PathVariable("messageId") String messageId, @RequestParam("userId") UUID userId) {
        return messageStore.deleteForUser(messageId, userId) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{messageId}/delete-for-all")
    @Operation(summary = "Delete message for all")
    public ResponseEntity<Void> deleteForAll(@PathVariable("messageId") String messageId) {
        return messageStore.deleteForAll(messageId) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/chat/{chatId}")
    @Operation(summary = "Get messages by chat")
    public ResponseEntity<List<MessagePayload>> getByChat(@PathVariable("chatId") UUID chatId, @RequestParam(value = "requesterId", required = false) UUID requesterId) {
        List<MessagePayload> list = messageStore.list(chatId).stream()
                .filter(m -> !m.isDeletedForAll())
                .filter(m -> requesterId == null || !m.getDeletedForUsers().contains(requesterId))
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{messageId}")
    @Operation(summary = "Get message by id")
    public ResponseEntity<MessagePayload> getById(@PathVariable("messageId") String messageId) {
        return messageStore.find(messageId)
                .filter(m -> !m.isDeletedForAll())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/chat/{chatId}/search")
    @Operation(summary = "Search messages in chat")
    public ResponseEntity<List<MessagePayload>> search(@PathVariable("chatId") UUID chatId, @RequestParam("q") String q) {
        return ResponseEntity.ok(messageStore.search(chatId, q));
    }

    @GetMapping("/chat/{chatId}/last")
    @Operation(summary = "Get last message in chat")
    public ResponseEntity<MessagePayload> last(@PathVariable("chatId") UUID chatId) {
        return messageStore.lastMessage(chatId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private ResponseEntity<MessagePayload> sendMessage(SendRequest req, MessageType explicitType, boolean system, String replyTo, String forwardFrom) {
        // Basic validation
        if (req.chatId == null) {
            return ResponseEntity.badRequest().build();
        }
        if (!system && req.senderId == null) {
            return ResponseEntity.badRequest().build();
        }
        if (!system && !authUserClient.userExists(req.senderId)) {
            return ResponseEntity.status(400).build();
        }
        if (blockRegistry.isPreventSend()) {
            return ResponseEntity.status(403).build();
        }
        if (!system && req.senderId != null && blockRegistry.isBlocked(req.senderId)) {
            return ResponseEntity.status(403).build();
        }

        MessageType resolvedType = explicitType != null ? explicitType : resolveType(req);

        // Require media meta for media messages
        if (isMedia(resolvedType) && (req.file == null || req.file.url == null || req.file.name == null)) {
            return ResponseEntity.badRequest().build();
        }

        MessagePayload.FileMeta fm = null;
        if (req.file != null) {
            fm = new MessagePayload.FileMeta();
            fm.setName(req.file.name);
            fm.setUrl(req.file.url);
            fm.setSize(req.file.size);
            fm.setSizeReadable(req.file.mime); // repurpose mime into readable slot for demo
        }

        String safeReply = replyTo == null ? null : replyTo;
        String safeForward = forwardFrom == null ? null : forwardFrom;

        MessagePayload payload = messageStore.create(req.chatId, system ? null : req.senderId, fallbackContent(req.content), resolvedType, fm, safeReply, system, safeForward);
        return ResponseEntity.ok(payload);
    }

    private String fallbackContent(String content) {
        return (content == null || content.isBlank()) ? "NOMA'LUM" : content;
    }

    private boolean isMedia(MessageType type) {
        return type == MessageType.IMAGE || type == MessageType.VIDEO || type == MessageType.VOICE || type == MessageType.FILE;
    }

    private MessageType resolveType(SendRequest req) {
        if (req.file == null) {
            return MessageType.TEXT;
        }
        String mime = req.file.mime == null ? "" : req.file.mime.toLowerCase();
        if (mime.startsWith("image/")) return MessageType.IMAGE;
        if (mime.startsWith("video/")) return MessageType.VIDEO;
        if (mime.startsWith("audio/") || mime.contains("voice")) return MessageType.VOICE;
        // fallback to FILE
        return MessageType.FILE;
    }

    public static class SendRequest {
        @Schema(description = "Chat id", required = true)
        public UUID chatId;
        @Schema(description = "Sender id (omit for system message)", required = false)
        public UUID senderId;
        @Schema(description = "Message content")
        public String content;
        @Schema(description = "Optional file meta")
        public FileReq file;
    }

    public static class ForwardRequest {
        @Schema(description = "Target chat", required = true)
        public UUID chatId;
        @Schema(description = "Sender", required = true)
        public UUID senderId;
    }

    public static class EditRequest {
        @Schema(description = "New content", required = true)
        public String content;
    }

    public static class FileReq {
        public String name;
        public String url;
        public Long size;
        public String mime;
    }

    private FileReq storeFile(String fileType, MultipartFile file, HttpServletRequest request) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
        try {
            var stored = fileStorageService.save(fileType, file);
            FileReq fr = new FileReq();
            fr.name = stored.filename();
            fr.url = buildPublicUrl(request, stored.relativePath());
            fr.size = stored.size();
            fr.mime = file.getContentType();
            return fr;
        } catch (Exception e) {
            throw new RuntimeException("File upload failed: " + e.getMessage(), e);
        }
    }

    private String buildPublicUrl(HttpServletRequest req, String relativePath) {
        String base = req.getScheme() + "://" + req.getServerName();
        int port = req.getServerPort();
        if (!(req.getScheme().equals("http") && port == 80) && !(req.getScheme().equals("https") && port == 443)) {
            base = base + ":" + port;
        }
        String publicBase = "/odnlicasjocdiahduhjcoinaurofrejdhiudosjkhfddddddddddddddddddddiopasdijkhieodfjhsiui0eodjifhureodihuosfdjfiles";
        return base + publicBase + "/" + relativePath;
    }
}
