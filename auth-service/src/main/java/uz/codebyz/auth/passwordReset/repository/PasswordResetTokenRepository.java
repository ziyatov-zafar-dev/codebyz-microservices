package uz.codebyz.auth.passwordReset.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.codebyz.auth.passwordReset.entity.PasswordResetToken;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    Optional<PasswordResetToken> findTopByUserIdAndUsedFalseOrderByExpiresAtDesc(UUID userId);
}

