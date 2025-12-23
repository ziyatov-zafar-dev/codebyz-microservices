package uz.codebyz.message.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.codebyz.message.service.UserService;
import uz.codebyz.message.service.impl.UserServiceImpl;
import uz.codebyz.message.dto.req.chat.CreateChatRequest;
import uz.codebyz.message.dto.response.chat.ChatResponse;
import uz.codebyz.message.dto.response.ResponseDto;
import uz.codebyz.message.security.JwtUser;
import uz.codebyz.message.service.ChatService;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
public class ChatRestController {
    private final ChatService chatService;
    private final UserService userService;

    public ChatRestController(ChatService chatService, UserServiceImpl userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    @GetMapping("my-chats")
    public ResponseDto<List<ChatResponse>> myChats(@AuthenticationPrincipal JwtUser jwtUser) throws Exception {
        return chatService.myChats(jwtUser.getUserId());
    }

    @GetMapping("get-user")
    public ResponseEntity<?> getUser(@AuthenticationPrincipal JwtUser jwtUser) throws Exception {
        return ResponseEntity.ok(userService.getUser(jwtUser.getUserId()));
    }

    @PostMapping("create-chat")
    public ResponseEntity<?> userinfo(@AuthenticationPrincipal JwtUser user, @RequestBody CreateChatRequest req) throws Exception {
        return ResponseEntity.ok(chatService.createChat(req, user.getUserId()));
    }
}
