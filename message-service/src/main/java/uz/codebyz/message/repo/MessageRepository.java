
package uz.codebyz.message.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.codebyz.message.mongo.Message;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    @Query(
            value = "{ 'chatId': ?0, 'deleted': false }",
            sort = "{ 'createdAt': -1 }"
    )
    List<Message> findAllByChatId(String chatId);
}
