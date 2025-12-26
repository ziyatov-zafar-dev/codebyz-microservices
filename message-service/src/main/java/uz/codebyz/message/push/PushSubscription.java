package uz.codebyz.message.push;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document(collection = "push_subscriptions")
public class PushSubscription {

    @Id
    private String id;

    @Indexed
    private UUID userId;

    @Indexed(unique = true)
    private String endpoint;
    private String p256dh;
    private String auth;

    @CreatedDate
    private Instant createdAt;

    public PushSubscription() {
    }

    public PushSubscription(UUID userId, String endpoint, String p256dh, String auth) {
        this.userId = userId;
        this.endpoint = endpoint;
        this.p256dh = p256dh;
        this.auth = auth;
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getP256dh() {
        return p256dh;
    }

    public String getAuth() {
        return auth;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
