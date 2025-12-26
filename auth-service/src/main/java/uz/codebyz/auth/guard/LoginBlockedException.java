package uz.codebyz.auth.guard;

import java.time.LocalDateTime;

public class LoginBlockedException extends RuntimeException {
    private final LocalDateTime blockedUntil;

    public LoginBlockedException(LocalDateTime blockedUntil) {
        super("Login is blocked until " + blockedUntil);
        this.blockedUntil = blockedUntil;
    }

    public LocalDateTime getBlockedUntil() { return blockedUntil; }
}
