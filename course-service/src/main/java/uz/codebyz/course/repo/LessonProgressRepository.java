package uz.codebyz.course.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.codebyz.course.entity.LessonProgress;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Dars progressi uchun repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, UUID> {
    @Query("select lp from LessonProgress lp where lp.lesson.id = :lessonId")
    List<LessonProgress> findByLessonId(@Param("lessonId") UUID lessonId); // Dars bo'yicha progresslar

    @Query("select lp from LessonProgress lp where lp.studentId = :studentId")
    List<LessonProgress> findByStudentId(@Param("studentId") UUID studentId); // O'quvchining barcha dars progresslari

    @Query("select lp from LessonProgress lp where lp.lesson.id = :lessonId and lp.studentId = :studentId")
    Optional<LessonProgress> findByLessonIdAndStudentId(@Param("lessonId") UUID lessonId, @Param("studentId") UUID studentId); // Bitta dars progressi
}
