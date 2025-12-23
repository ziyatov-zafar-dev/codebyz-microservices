package uz.codebyz.auth.verification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, UUID> {
    @Query("""
            select ev from EmailVerification ev
            where ev.email = ?1 and ev.purpose = ?2 and ev.used=false
            order by ev.createdAt desc limit 1
            """)
    Optional<EmailVerification> findLatest(String email, VerificationPurpose purpose);

    Optional<EmailVerification> findFirstByEmailAndPurposeAndUsedFalseOrderByExpiresAtDesc(
            String email,
            VerificationPurpose purpose
    );

    @Modifying
    @Query("""
                update EmailVerification ev
                set ev.used = true
                where ev.email = :email
                  and ev.purpose = :purpose
                  and ev.used = false
            """)
    int deactivateOld(
            @Param("email") String email,
            @Param("purpose") VerificationPurpose purpose
    );



}
