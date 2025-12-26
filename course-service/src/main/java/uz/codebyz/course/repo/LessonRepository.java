package uz.codebyz.course.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.codebyz.course.entity.Lesson;

import java.util.List;
import java.util.UUID;

// Darslar uchun repository
public interface LessonRepository extends JpaRepository<Lesson, UUID> {
    @Query("select l from Lesson l where l.module.id = :moduleId order by l.orderIndex")
    List<Lesson> findByModuleIdOrderByOrderIndex(@Param("moduleId") UUID moduleId); // Moduldagi darslar tartibi
}
