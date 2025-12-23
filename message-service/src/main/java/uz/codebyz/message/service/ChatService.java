package uz.codebyz.message.service;

import uz.codebyz.message.dto.req.chat.CreateChatRequest;
import uz.codebyz.message.dto.response.chat.ChatResponse;
import uz.codebyz.message.dto.response.ResponseDto;

import java.util.List;
import java.util.UUID;

public interface ChatService {
    ResponseDto<List<ChatResponse>> myChats(UUID userId) throws Exception;
    ResponseDto<ChatResponse> createChat(CreateChatRequest req,UUID userid)  throws Exception;
//    ResponseDto<ChatResponse> deleteChat()  throws Exception;

}
