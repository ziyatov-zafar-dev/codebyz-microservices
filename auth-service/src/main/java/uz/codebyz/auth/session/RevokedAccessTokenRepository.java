package uz.codebyz.auth.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface RevokedAccessTokenRepository extends JpaRepository<RevokedAccessToken, String> {

    boolean existsByJti(String jti);

    @Modifying
    @Query("delete from RevokedAccessToken r where r.userId=?1")
    void revokeAllByUser(UUID userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
                update RevokedAccessToken t
                set t.revokedAt = CURRENT_TIMESTAMP
                where t.userId = :userId
                  and t.deviceId <> :deviceId
            """)
    void revokeAllExceptDevice(
            @Param("userId") UUID uid,
            @Param("deviceId") String currentDeviceId
    );





}
