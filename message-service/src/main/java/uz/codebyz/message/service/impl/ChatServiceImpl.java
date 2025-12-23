package uz.codebyz.message.service.impl;

import org.springframework.stereotype.Service;
import uz.codebyz.message.dto.req.chat.CreateChatRequest;
import uz.codebyz.message.dto.response.chat.ChatResponse;
import uz.codebyz.message.dto.response.ResponseDto;
import uz.codebyz.message.dto.enums.ErrorCode;
import uz.codebyz.message.entity.Chat;
import uz.codebyz.message.mapper.ChatMapper;
import uz.codebyz.message.repo.ChatRepository;
import uz.codebyz.message.repo.MessageRepository;
import uz.codebyz.message.service.ChatService;
import uz.codebyz.message.service.UserService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;
    private final UserService userService;
    private final MessageRepository messageRepository;

    public ChatServiceImpl(ChatRepository chatRepository, ChatMapper chatMapper, UserServiceImpl userService, MessageRepository messageRepository) {
        this.chatRepository = chatRepository;
        this.chatMapper = chatMapper;
        this.userService = userService;
        this.messageRepository = messageRepository;
    }

    @Override
    public ResponseDto<List<ChatResponse>> myChats(UUID userId) throws Exception {
        if (!existsUser(userId)) {
            return ResponseDto.fail(403, ErrorCode.NOT_FOUND_USER, ErrorCode.NOT_FOUND_USER.getDescription());
        }
        List<Chat> chats = chatRepository.findMyChats(userId);
        List<ChatResponse> chatResponses = chats.stream().map(
                chat -> chatMapper.toDto(chat, messageRepository.findAllByChatId(chat.getId().toString()))
        ).collect(Collectors.toList());
        return ResponseDto.ok("Success", chatResponses);
    }

    @Override
    public ResponseDto<ChatResponse> createChat(CreateChatRequest req, UUID userId) {

        // 1️⃣ user1 — token egasi
        req.setUser1Id(userId);

        // 2️⃣ O‘zing bilan chat ochish mumkin emas
        if (req.getUser1Id().equals(req.getUser2Id())) {
            return ResponseDto.fail(
                    400,
                    ErrorCode.BAD_REQUEST,
                    "User cannot create chat with himself"
            );
        }

        // 3️⃣ Userlar mavjudligini tekshirish
        if (!existsUser(req.getUser1Id())) {
            return ResponseDto.fail(
                    404,
                    ErrorCode.NOT_FOUND_USER,
                    "user1 -> " + ErrorCode.NOT_FOUND_USER.getDescription()
            );
        }

        if (!existsUser(req.getUser2Id())) {
            return ResponseDto.fail(
                    404,
                    ErrorCode.NOT_FOUND_USER,
                    "user2 -> " + ErrorCode.NOT_FOUND_USER.getDescription()
            );
        }

        // 4️⃣ Mavjud chatni topish (A,B) yoki (B,A)
        Optional<Chat> optionalChat =
                chatRepository.findByUser1IdAndUser2Id(
                        req.getUser1Id(),
                        req.getUser2Id()
                );

        if (optionalChat.isPresent()) {
            Chat chat = optionalChat.get();

            // 5️⃣ BLOCK tekshiruvi (kimdir bloklagan bo‘lsa — STOP)
            boolean blocked =
                    chat.getBlockUser1Id() != null
                            || chat.getBlockUser2Id() != null;

            if (blocked) {
                return ResponseDto.fail(
                        403,
                        ErrorCode.BAD_REQUEST,
                        "Chat is blocked"
                );
            }

            boolean deletedByUser1 = chat.getDeletedUser1Id() != null;
            boolean deletedByUser2 = chat.getDeletedUser2Id() != null;

            // 6️⃣ IKKALA TOMON HAM DELETE QILGAN → QAYTA OCHAMIZ ✅
            if (deletedByUser1 && deletedByUser2) {
                chat.setDeletedUser1Id(null);
                chat.setDeletedUser2Id(null);
                chat.setUpdatedAt(Instant.now());

                Chat saved = chatRepository.save(chat);

                return ResponseDto.ok(
                        "Chat reactivated",
                        chatMapper.toDto(saved, new ArrayList<>())
                );
            }

            // 7️⃣ Aks holda chat allaqachon mavjud
            return ResponseDto.fail(
                    409,
                    ErrorCode.CHAT_ALREADY_EXISTS,
                    ErrorCode.CHAT_ALREADY_EXISTS.getDescription()
            );
        }

        // 8️⃣ CHAT YO‘Q → YANGI CHAT YARATAMIZ
        Chat chat = new Chat();
        chat.setUser1Id(req.getUser1Id());
        chat.setUser2Id(req.getUser2Id());
        chat.setLastMessageId(null);
        chat.setLastMessageTime(null);
        chat.setMutedByUser1(false);
        chat.setMutedByUser2(false);
        chat.setCreatedAt(Instant.now());
        chat.setUpdatedAt(Instant.now());
        chat.setDeletedUser1Id(null);
        chat.setDeletedUser2Id(null);
        chat.setBlockUser1Id(null);
        chat.setBlockUser2Id(null);

        Chat savedChat = chatRepository.save(chat);

        return ResponseDto.ok(
                "Chat created",
                chatMapper.toDto(savedChat, new ArrayList<>())
        );
    }



    private boolean existsUser(UUID userId) {
        return userService.userExists(userId);
    }
}
