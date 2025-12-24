package uz.codebyz.message.service;

import org.springframework.stereotype.Service;
import uz.codebyz.message.domain.SystemAction;
import uz.codebyz.message.dto.command.BlockCommand;
import uz.codebyz.message.security.JwtUser;

@Service
public class BlockService {

    private final ChatDirectory chatDirectory;
    private final SystemMessageService systemMessageService;

    public BlockService(ChatDirectory chatDirectory, SystemMessageService systemMessageService) {
        this.chatDirectory = chatDirectory;
        this.systemMessageService = systemMessageService;
    }

    public void handle(JwtUser user, BlockCommand cmd) {
        chatDirectory.find(cmd.getChatId()).ifPresent(chat -> {
            if (!chat.isParticipant(user.getUserId())) {
                return;
            }
            if (cmd.isBlock()) {
                chat.setBlockedBy(user.getUserId());
                systemMessageService.systemForAction(chat, SystemAction.USER_BLOCKED);
            } else {
                chat.setBlockedBy(null);
                systemMessageService.systemForAction(chat, SystemAction.USER_UNBLOCKED);
            }
        });
    }
}
