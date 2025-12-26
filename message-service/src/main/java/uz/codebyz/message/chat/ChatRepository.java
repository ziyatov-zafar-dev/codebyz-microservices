package uz.codebyz.message.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {
    Optional<Chat> findByUser1IdAndUser2Id(UUID user1Id, UUID user2Id);

    List<Chat> findByUser1IdOrUser2Id(UUID userId1, UUID userId2);
}
