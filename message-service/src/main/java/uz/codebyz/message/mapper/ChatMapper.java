package uz.codebyz.message.mapper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import uz.codebyz.message.dto.response.chat.ChatResponse;
import uz.codebyz.message.dto.response.message.MessageResponse;
import uz.codebyz.message.entity.Chat;
import uz.codebyz.message.mongo.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChatMapper {
    private final MessageMapper messageMapper;

    public ChatMapper(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    public ChatResponse toDto(Chat chat, List<Message> messages) {
        return new ChatResponse(
                chat.getId(),
                chat.getUser1Id(),
                chat.getUser2Id(),
                chat.getLastMessageId(),
                chat.getLastMessageTime(),
                chat.isMutedByUser1(),
                chat.isMutedByUser2(),
                chat.getCreatedAt(),
                chat.getUpdatedAt(),
                chat.getBlockUser1Id(),
                chat.getBlockUser2Id(),
                messages == null || messages.isEmpty() ? new ArrayList<>() : messageMapper.toDto(messages)
        );
    }
}
