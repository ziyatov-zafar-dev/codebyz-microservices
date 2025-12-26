package uz.codebyz.message.web;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.codebyz.message.chat.Chat;
import uz.codebyz.message.chat.ChatService;
import uz.codebyz.message.dto.ChatResponse;
import uz.codebyz.message.dto.MessageResponse;
import uz.codebyz.message.dto.SendMessageRequest;
import uz.codebyz.message.dto.TypingRequest;
import uz.codebyz.message.dto.TypingResponse;
import uz.codebyz.message.message.MessageService;
import uz.codebyz.message.security.JwtPrincipal;
import uz.codebyz.message.security.JwtUser;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chats") // REST ildiz yo'li
public class ChatController {

    private final ChatService chatService;       // Chatlarni boshqaruvchi servis
    private final MessageService messageService; // Xabar yuborish/o'qish servisi

    public ChatController(ChatService chatService, MessageService messageService) {
        this.chatService = chatService;
        this.messageService = messageService;
    }

    @PostMapping("/send") // REST orqali xabar yuborish
    public ResponseEntity<uz.codebyz.message.dto.ResponseDto<MessageResponse>> sendMessage(@Valid @RequestBody SendMessageRequest request,
                                                       Principal principal) {
        UUID senderId = extractUserId(principal); // JWT dan foydalanuvchi
        MessageResponse response = messageService.send(senderId, request); // Servisga yuborish
        return ResponseEntity.ok(uz.codebyz.message.dto.ResponseDto.ok("OK", response)); // o'ram bilan qaytarish
    }

    @GetMapping // Auth user uchun chatlar ro'yxati
    public ResponseEntity<uz.codebyz.message.dto.ResponseDto<List<ChatResponse>>> myChats(Principal principal) {
        UUID userId = extractUserId(principal); // JWT foydalanuvchisi
        List<ChatResponse> chats = chatService.listForUser(userId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(uz.codebyz.message.dto.ResponseDto.ok("OK", chats));
    }

    @GetMapping("/{chatId}/messages") // Chatdagi xabarlar
    public ResponseEntity<uz.codebyz.message.dto.ResponseDto<List<MessageResponse>>> chatMessages(@PathVariable("chatId") UUID chatId, Principal principal) {
        UUID userId = extractUserId(principal); // JWT foydalanuvchisi
        List<MessageResponse> messages = messageService.getMessages(chatId, userId); // Servisdan o'qish
        return ResponseEntity.ok(uz.codebyz.message.dto.ResponseDto.ok("OK", messages)); // o'ram bilan
    }

    @MessageMapping("/send") // STOMP orqali yuborish
    public void sendViaStomp(@Payload SendMessageRequest request, SimpMessageHeaderAccessor headers) {
        Principal principal = headers.getUser(); // STOMP user (handshake)
        if (principal == null && headers.getSessionAttributes() != null) { // fallback session atributdan
            Object sessionPrincipal = headers.getSessionAttributes().get("principal");
            if (sessionPrincipal instanceof Principal p) {
                principal = p;
            }
        }
        if (principal == null) { // Authsiz STOMP
            throw new uz.codebyz.message.exception.BadRequestException("STOMP foydalanuvchi aniqlanmadi");
        }
        UUID senderId = extractUserId(principal); // JWT foydalanuvchisi
        messageService.send(senderId, request); // Xabar yuborish va push
    }

    @MessageMapping("/typing") // STOMP typing indikator
    public void typing(@Payload TypingRequest request, SimpMessageHeaderAccessor headers) {
        Principal principal = headers.getUser();
        if (principal == null && headers.getSessionAttributes() != null) {
            Object sessionPrincipal = headers.getSessionAttributes().get("principal");
            if (sessionPrincipal instanceof Principal p) {
                principal = p;
            }
        }
        if (principal == null || request.getReceiverId() == null) {
            return;
        }
        UUID senderId = extractUserId(principal);
        TypingResponse payload = new TypingResponse(senderId, request.isTyping());
        messageService.pushTyping(request.getReceiverId(), payload);
    }

    private UUID extractUserId(Principal principal) { // JWT subjectni olish
        if (principal instanceof JwtPrincipal jwtPrincipal) {
            return jwtPrincipal.getUserId();
        }
        if (principal instanceof JwtUser jwtUser) {
            return jwtUser.getUserId();
        }
        return UUID.fromString(principal.getName()); // fallback
    }

    private ChatResponse toResponse(Chat chat) { // Entity -> DTO
        return new ChatResponse(chat.getId(), chat.getUser1Id(), chat.getUser2Id(), chat.getCreatedAt(), chat.getLastMessageAt());
    }
}
