package uz.codebyz.auth.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    @Query("select rt from RefreshToken rt where rt.userId=?1 and rt.revoked=false and rt.expiresAt> ?2")
    List<RefreshToken> findActiveByUser(UUID userId, Instant now);

    @Modifying
    @Query("update RefreshToken rt set rt.revoked=true where rt.userId=?1 and rt.deviceId=?2 and rt.revoked=false")
    int revokeByUserAndDevice(UUID userId, String deviceId);

    @Modifying
    @Query("update RefreshToken rt set rt.revoked=true where rt.userId=?1 and rt.revoked=false")
    int revokeAllByUser(UUID userId);

    @Modifying
    @Query("delete from RefreshToken rt where rt.expiresAt < ?1")
    int deleteExpired(Instant now);

    @Modifying
    @Query("""
                update RefreshToken rt
                set rt.revoked = true
                where rt.userId = :userId
                  and rt.revoked = false
                  and rt.deviceId <> :deviceId
            """)
    void revokeAllExceptDevice(
            @Param("userId") UUID userId,
            @Param("deviceId") String currentDeviceId
    );

    Optional<RefreshToken> findByJtiAndDeviceIdAndRevokedFalse(String jti, String deviceId);
}
