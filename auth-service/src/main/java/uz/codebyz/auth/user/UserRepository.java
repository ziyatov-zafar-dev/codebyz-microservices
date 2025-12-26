package uz.codebyz.auth.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);

    @Query("select u from User u where lower(u.email)=lower(?1) or lower(u.username)=lower(?1)")
    Optional<User> findByEmailOrUsername(String identifier);
    @Query("select u.tokenVersion from User u where u.id = ?1")
    Integer findTokenVersionById(UUID userId);

}
