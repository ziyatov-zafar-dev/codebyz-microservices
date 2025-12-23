package uz.codebyz.auth.guard;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserLoginGuardRepository extends JpaRepository<UserLoginGuard, UUID> {
    Optional<UserLoginGuard> findByUserId(UUID userId);
    void deleteByUserId(UUID userId);

}
