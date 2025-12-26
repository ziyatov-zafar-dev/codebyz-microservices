package uz.codebyz.message.chat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.codebyz.message.exception.ForbiddenOperationException;
import uz.codebyz.message.exception.ResourceNotFoundException;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Transactional
    public Chat getOrCreate(UUID userId, UUID otherUserId) {
        var pair = normalize(userId, otherUserId);
        return chatRepository.findByUser1IdAndUser2Id(pair.first(), pair.second())
                .orElseGet(() -> chatRepository.save(new Chat(pair.first(), pair.second())));
    }

    @Transactional(readOnly = true)
    public Chat getByIdForUser(UUID chatId, UUID userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat topilmadi"));
        if (!isParticipant(chat, userId)) {
            throw new ForbiddenOperationException("Chatga kirish taqiqlangan");
        }
        return chat;
    }

    @Transactional(readOnly = true)
    public List<Chat> listForUser(UUID userId) {
        return chatRepository.findByUser1IdOrUser2Id(userId, userId);
    }

    @Transactional
    public void touchLastMessage(Chat chat, Instant instant) {
        chat.setLastMessageAt(instant);
        chatRepository.save(chat);
    }

    private boolean isParticipant(Chat chat, UUID userId) {
        return chat.getUser1Id().equals(userId) || chat.getUser2Id().equals(userId);
    }

    private Pair normalize(UUID user1, UUID user2) {
        if (user1.equals(user2)) {
            throw new ForbiddenOperationException("O'zingizga xabar yubora olmaysiz");
        }
        return UUID_NAME_COMPARATOR.compare(user1, user2) <= 0
                ? new Pair(user1, user2)
                : new Pair(user2, user1);
    }

    private record Pair(UUID first, UUID second) {
    }

    private static final Comparator<UUID> UUID_NAME_COMPARATOR = Comparator.comparing(UUID::toString);
}
