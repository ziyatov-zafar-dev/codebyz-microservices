package uz.codebyz.auth.guard;

import java.time.Instant;

public class LoginBlockedException extends RuntimeException {
    private final Instant blockedUntil;

    public LoginBlockedException(Instant blockedUntil) {
        super("Login is blocked until " + blockedUntil);
        this.blockedUntil = blockedUntil;
    }

    public Instant getBlockedUntil() { return blockedUntil; }
}
