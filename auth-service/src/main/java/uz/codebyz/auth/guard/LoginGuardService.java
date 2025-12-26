package uz.codebyz.auth.guard;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class LoginGuardService {
    private final UserLoginGuardRepository repo;

    public LoginGuardService(UserLoginGuardRepository repo) { this.repo = repo; }

    public void ensureNotBlocked(UUID userId) {
        UserLoginGuard g = repo.findByUserId(userId).orElse(null);
        if (g == null) return;
        LocalDateTime until = g.getBlockedUntil();
        if (until != null && until.isAfter(LocalDateTime.now())) throw new LoginBlockedException(until);
    }

    public void onSuccess(UUID userId) {
        UserLoginGuard g = repo.findByUserId(userId).orElse(null);
        if (g == null) return;
        g.setFailCount(0);
        g.setBlockedUntil(LocalDateTime.MIN);
        repo.save(g);
    }

    public void onFail(UUID userId) {
        UserLoginGuard g = repo.findByUserId(userId).orElse(null);
        if (g == null) {
            g = new UserLoginGuard();
            g.setUserId(userId);
            g.setFailCount(0);
            g.setBlockedUntil(LocalDateTime.MIN);
        }

        int fail = g.getFailCount() + 1;
        g.setFailCount(fail);

        Duration add;
        if (fail == 3) add = Duration.ofHours(1);
        else if (fail == 6) add = Duration.ofHours(2);
        else if (fail > 6) add = Duration.ofHours(1);
        else add = Duration.ZERO;

        if (!add.isZero()) {
            LocalDateTime base = LocalDateTime.now();
            LocalDateTime cur = g.getBlockedUntil();
            if (cur != null && cur.isAfter(base)) base = cur;
            g.setBlockedUntil(base.plusSeconds(add.getSeconds()));
        }
        repo.save(g);
    }
}
