package uz.codebyz.auth.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface RevokedAccessTokenRepository extends JpaRepository<RevokedAccessToken, String> {

    boolean existsByJti(String jti);

    @Modifying
    @Query("delete from RevokedAccessToken r where r.userId=?1")
    void revokeAllByUser(UUID userId);
}
