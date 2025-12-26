package uz.codebyz.message.security;

import java.security.Principal;
import java.util.UUID;

public class JwtPrincipal implements Principal {

    private final UUID userId;

    public JwtPrincipal(UUID userId) {
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }

    @Override
    public String getName() {
        return userId.toString();
    }
}
