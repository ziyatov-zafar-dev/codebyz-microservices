
package uz.codebyz.message.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.codebyz.message.entity.Chat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {
    @Query("""
                select c from Chat c
                where (c.user1Id = :userId and c.deletedUser1Id is null)
                   or (c.user2Id = :userId and c.deletedUser1Id is null)
                order by c.lastMessageTime desc nulls last
            """)
    List<Chat> findMyChats(@Param("userId") UUID userId);


    @Query("""
                SELECT c
                FROM Chat c
                WHERE
                    (c.user1Id = :user1Id AND c.user2Id = :user2Id)
                 OR (c.user1Id = :user2Id AND c.user2Id = :user1Id)
            """)
    Optional<Chat> findByUser1IdAndUser2Id(
            @Param("user1Id") UUID user1Id,
            @Param("user2Id") UUID user2Id
    );

    @Query("""
                SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
                FROM Chat c
                WHERE 
                    (c.user1Id = :user1Id AND c.user2Id = :user2Id AND c.blockUser1Id = :user1Id)
                 OR (c.user1Id = :user2Id AND c.user2Id = :user1Id AND c.blockUser2Id = :user1Id)
            """)
    boolean isUserBlockedByMe(
            @Param("user1Id") UUID user1Id,
            @Param("user2Id") UUID user2Id
    );

}
