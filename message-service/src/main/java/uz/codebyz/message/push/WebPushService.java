package uz.codebyz.message.push;


import com.fasterxml.jackson.databind.ObjectMapper;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uz.codebyz.message.config.WebPushProperties;
import uz.codebyz.message.dto.MessageResponse;
import uz.codebyz.message.dto.PushSubscriptionRequest;

import java.security.Security;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class WebPushService {

    private static final Logger log = LoggerFactory.getLogger(WebPushService.class);
    private final PushSubscriptionRepository repository;
    private boolean enabled;
    private PushService pushService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WebPushService(PushSubscriptionRepository repository,
                          WebPushProperties properties) {
        this.repository = repository;
        String publicKey = properties.getPublicKey() == null ? "" : properties.getPublicKey();
        String privateKey = properties.getPrivateKey() == null ? "" : properties.getPrivateKey();
        String subject = properties.getSubject() == null ? "mailto:admin@example.com" : properties.getSubject();
        initPushService(publicKey, privateKey, subject);
    }

    public void saveSubscription(UUID userId, PushSubscriptionRequest request) {
        PushSubscription sub = new PushSubscription(
                userId,
                request.getEndpoint(),
                request.getKeys().getP256dh(),
                request.getKeys().getAuth()
        );
        repository.save(sub);
    }

    public void sendNewMessageNotification(UUID userId, MessageResponse message) {
        if (!enabled) return;
        List<PushSubscription> subs = repository.findByUserId(userId);
        if (subs.isEmpty()) return;
        for (PushSubscription sub : subs) {
            try {
                byte[] payload = buildPayload(message);
                Notification notification = new Notification(
                        sub.getEndpoint(),
                        sub.getP256dh(),
                        sub.getAuth(),
                        payload
                );
                pushService.send(notification);
            } catch (Exception ex) {
                // agar yuborib bo'lmasa, subscriptionni o'chiramiz
                log.warn("Push yuborib bo'lmadi, subscription o'chiriladi: {}", sub.getEndpoint());
                repository.delete(sub);
            }
        }
    }

    private void initPushService(String publicKey, String privateKey, String subject) {
        if (publicKey.isBlank() || privateKey.isBlank()) {
            this.pushService = null;
            this.enabled = false;
            log.warn("WebPush disabled: VAPID keys are missing");
            return;
        }
        try {
            Security.addProvider(new BouncyCastleProvider());
            this.pushService = new PushService(publicKey.trim(), privateKey.trim(), subject);
            this.enabled = true;
        } catch (Exception ex) {
            this.pushService = null;
            this.enabled = false;
            log.warn("WebPush disabled: invalid VAPID keys ({})", ex.getMessage());
        }
    }

    private byte[] buildPayload(MessageResponse message) throws Exception {
        Map<String, Object> body = Map.of(
                "title", "Yangi xabar",
                "body", message.getContent(),
                "chatId", message.getChatId(),
                "senderId", message.getSenderId(),
                "messageId", message.getMessageId()
        );
        return objectMapper.writeValueAsBytes(body);
    }
}
