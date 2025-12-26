package uz.codebyz.course.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.codebyz.course.entity.LessonContent;

import java.util.Optional;
import java.util.UUID;

// Dars kontenti uchun repository
public interface LessonContentRepository extends JpaRepository<LessonContent, UUID> {
    @Query("select lc from LessonContent lc where lc.lesson.id = :lessonId")
    Optional<LessonContent> findByLessonId(@Param("lessonId") UUID lessonId); // Dars ID bo'yicha
}
