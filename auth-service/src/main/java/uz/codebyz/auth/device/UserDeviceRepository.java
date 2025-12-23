package uz.codebyz.auth.device;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDeviceRepository extends JpaRepository<UserDevice, UUID> {
    @Query("select d from UserDevice d where d.userId=?1 and d.active=true")
    List<UserDevice> findActiveByUserId(UUID userId);

    @Query("select count(d) from UserDevice d where d.userId=?1 and d.active=true")
    long countActiveByUserId(UUID userId);

    Optional<UserDevice> findByUserIdAndDeviceId(UUID userId, String deviceId);


    @Query("select d from UserDevice d where d.userId=:uid and d.active=true order by d.createdAt desc")
    List<UserDevice> findByUserId(@Param("uid") UUID userId);

    boolean existsByUserIdAndDeviceId(UUID userId, String deviceId);
    @Modifying
    @Query("update UserDevice d set d.active=false where d.userId=?1")
    void deactivateAllByUser(UUID userId);


}
