package uz.codebyz.message.mapper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import uz.codebyz.message.dto.response.message.MessageResponse;
import uz.codebyz.message.mongo.Message;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MessageMapper {
    public MessageResponse toDto(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getChatId(),
                message.getSenderId(),
                message.getContent(),
                message.getType(),
                message.getFileName(),
                message.getFilePath(),
                message.getFileUrl(),
                message.getFileSizeMB(),
                message.getFileSize(),
                message.getCreatedAt(),
                message.isEdited(),
                message.getEditedAt(),
                message.isRead(),
                message.getReadAt(),
                message.getReplyToMessageId(),
                message.getReactions()
        );
    }
    public List<MessageResponse> toDto(List<Message> messages) {
        return messages.stream().map(this::toDto).collect(Collectors.toList());
    }
    public Page<MessageResponse> toDto(Page<Message> messages) {
        return messages.map(this::toDto);
    }
}
