package uz.codebyz.course.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.codebyz.course.entity.LessonResource;

import java.util.List;
import java.util.UUID;

// Dars resurslari uchun repository
public interface LessonResourceRepository extends JpaRepository<LessonResource, UUID> {
    @Query("select lr from LessonResource lr where lr.lesson.id = :lessonId")
    List<LessonResource> findByLessonId(@Param("lessonId") UUID lessonId); // Darsga tegishli resurslar

    @Modifying
    @Query("delete from LessonResource lr where lr.lesson.id = :lessonId")
    void deleteByLessonId(@Param("lessonId") UUID lessonId); // Dars resurslarini o'chirish
}
