package uz.codebyz.message.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "webpush.vapid")
public class WebPushProperties {
    /**
     * VAPID public key (base64url)
     */
    private String publicKey;
    /**
     * VAPID private key (base64url)
     */
    private String privateKey;
    /**
     * Subject (email yoki URL)
     */
    private String subject = "mailto:admin@example.com";

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
