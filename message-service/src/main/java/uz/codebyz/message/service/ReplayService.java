package uz.codebyz.message.service;

import org.springframework.stereotype.Service;
import uz.codebyz.message.dto.command.EventAckCommand;
import uz.codebyz.message.security.JwtUser;
import uz.codebyz.message.ws.EventBroadcaster;

@Service
public class ReplayService {

    private final ChatDirectory chatDirectory;
    private final EventBroadcaster broadcaster;

    public ReplayService(ChatDirectory chatDirectory, EventBroadcaster broadcaster) {
        this.chatDirectory = chatDirectory;
        this.broadcaster = broadcaster;
    }

    public void replay(JwtUser user, EventAckCommand cmd) {
        chatDirectory.find(cmd.getChatId()).ifPresent(chat -> {
            if (!chat.isParticipant(user.getUserId())) {
                return;
            }
            chat.replayFrom(cmd.getLastEventSeq())
                    .forEach(envelope -> broadcaster.sendToUser(envelope, user.getUserId()));
        });
    }
}
