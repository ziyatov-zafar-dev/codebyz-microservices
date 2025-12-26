package uz.codebyz.message.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import uz.codebyz.message.chat.Chat;
import uz.codebyz.message.chat.ChatService;
import uz.codebyz.message.message.ChatMessage;
import uz.codebyz.message.message.ChatMessageRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class SeedDataInitializer implements ApplicationRunner {

    private static final UUID USER1 = UUID.fromString("42c8a45f-7fb3-4474-a126-043cc309076d");
    private static final UUID USER2 = UUID.fromString("3f675b19-df75-4437-bd54-ba5babf116c3");
    private static final int TARGET_COUNT = 70;

    private final ChatService chatService;
    private final ChatMessageRepository chatMessageRepository;

    public SeedDataInitializer(ChatService chatService, ChatMessageRepository chatMessageRepository) {
        this.chatService = chatService;
        this.chatMessageRepository = chatMessageRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        Chat chat = chatService.getOrCreate(USER1, USER2);
        List<ChatMessage> existing = chatMessageRepository.findByChatIdOrderByCreatedAtAsc(chat.getId());
        if (existing.size() >= TARGET_COUNT) {
            return;
        }
        int startIndex = existing.size();
        Instant baseTime = Instant.now().minusSeconds(3600);
        List<ChatMessage> toSave = new ArrayList<>();
        for (int i = startIndex; i < TARGET_COUNT; i++) {
            UUID sender = (i % 2 == 0) ? USER1 : USER2;
            UUID receiver = (i % 2 == 0) ? USER2 : USER1;
            ChatMessage msg = new ChatMessage(chat.getId(), sender, receiver, "Seed message " + (i + 1));
            msg.setCreatedAt(baseTime.plusSeconds(i * 30));
            toSave.add(msg);
        }
        chatMessageRepository.saveAll(toSave);
        chatService.touchLastMessage(chat, toSave.get(toSave.size() - 1).getCreatedAt());
    }
}
