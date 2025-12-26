package uz.codebyz.message.message;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.codebyz.message.auth.UserLookupClient;
import uz.codebyz.message.chat.Chat;
import uz.codebyz.message.chat.ChatService;
import uz.codebyz.message.dto.MessageResponse;
import uz.codebyz.message.dto.NotificationResponse;
import uz.codebyz.message.dto.SendMessageRequest;
import uz.codebyz.message.dto.TypingResponse;
import uz.codebyz.message.push.WebPushService;
import uz.codebyz.message.exception.BadRequestException;

import java.util.List;
import java.util.UUID;

@Service // Biznes mantiq servisi
public class MessageService {

    private final ChatService chatService; // Chat CRUD va nazorat
    private final ChatMessageRepository chatMessageRepository; // Mongo repository
    private final UserLookupClient userLookupClient; // Auth-service tekshiruv
    private final SimpMessagingTemplate messagingTemplate; // STOMP pushlar
    private final WebPushService webPushService; // Brauzer yopiq holat uchun push

    public MessageService(ChatService chatService,
                          ChatMessageRepository chatMessageRepository,
                          UserLookupClient userLookupClient,
                          SimpMessagingTemplate messagingTemplate,
                          WebPushService webPushService) {
        this.chatService = chatService;
        this.chatMessageRepository = chatMessageRepository;
        this.userLookupClient = userLookupClient;
        this.messagingTemplate = messagingTemplate;
        this.webPushService = webPushService;
    }

    @Transactional // Xabar yuborish tranzaksiyasi
    public MessageResponse send(UUID senderId, SendMessageRequest request) {
        UUID receiverId = request.getReceiverId(); // Kimga jo'natiladi
        if (receiverId == null) {
            throw new BadRequestException("Qabul qiluvchi ko'rsatilmagan");
        }
        // receiver tekshiruvi (hozircha o'chirilgan)
        // ensureReceiverExists(receiverId);

        Chat chat = chatService.getOrCreate(senderId, receiverId); // Chatni topish yoki yaratish
        ChatMessage message = new ChatMessage(chat.getId(), senderId, receiverId, request.getContent()); // Model tayyorlash
        chatMessageRepository.save(message); // Mongo'ga saqlash

        chatService.touchLastMessage(chat, message.getCreatedAt()); // Chat last_message_at ni yangilash

        MessageResponse response = toResponse(message); // DTO ga map
        // Asosiy push: qabul qiluvchiga user-destination
        messagingTemplate.convertAndSendToUser(receiverId.toString(), "/queue/messages", response);
        // Fallback push: umumiy topic (userId bo'yicha)
        messagingTemplate.convertAndSend("/topic/users/" + receiverId + "/messages", response);
        // Echo: jo'natuvchiga ham push
        messagingTemplate.convertAndSendToUser(senderId.toString(), "/queue/messages", response);

        // Notifikatsiya payload
        NotificationResponse notification = new NotificationResponse(
                "NEW_MESSAGE",
                response.getMessageId(),
                response.getChatId(),
                senderId,
                receiverId,
                response.getContent(),
                response.getCreatedAt()
        );
        // Notifikatsiyalar (user-destination va topic fallback)
        messagingTemplate.convertAndSendToUser(receiverId.toString(), "/queue/notifications", notification);
        messagingTemplate.convertAndSend("/topic/users/" + receiverId + "/notifications", notification);
        messagingTemplate.convertAndSendToUser(senderId.toString(), "/queue/notifications", notification);
        // Web Push (brauzer yopiq holat uchun)
        webPushService.sendNewMessageNotification(receiverId, response);
        return response;
    }

    @Transactional(readOnly = true) // Xabarlar tarixini o'qish
    public List<MessageResponse> getMessages(UUID chatId, UUID requesterId) {
        Chat chat = chatService.getByIdForUser(chatId, requesterId); // Kirish huquqini tekshirish
        return chatMessageRepository.findByChatIdOrderByCreatedAtAsc(chat.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void ensureReceiverExists(UUID receiverId) { // Auth-service orqali mavjudligini tekshirish
        if (!userLookupClient.exists(receiverId)) {
            throw new BadRequestException("Qabul qiluvchi topilmadi");
        }
    }

    private MessageResponse toResponse(ChatMessage message) { // Entity -> DTO map
        return new MessageResponse(
                message.getId(),
                message.getChatId(),
                message.getSenderId(),
                message.getReceiverId(),
                message.getContent(),
                message.getCreatedAt(),
                message.isRead()
        );
    }

    public void pushTyping(UUID receiverId, TypingResponse payload) {
        // Asosiy channel (user destination)
        messagingTemplate.convertAndSendToUser(receiverId.toString(), "/queue/typing", payload);
        // Fallback topic
        messagingTemplate.convertAndSend("/topic/users/" + receiverId + "/typing", payload);
    }
}
