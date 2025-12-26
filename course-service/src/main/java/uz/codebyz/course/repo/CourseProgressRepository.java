package uz.codebyz.course.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.codebyz.course.entity.CourseProgress;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Kurs progressi uchun repository
public interface CourseProgressRepository extends JpaRepository<CourseProgress, UUID> {
    @Query("select cp from CourseProgress cp where cp.course.id = :courseId")
    List<CourseProgress> findByCourseId(@Param("courseId") UUID courseId); // Kurs bo'yicha progresslar

    @Query("select cp from CourseProgress cp where cp.studentId = :studentId")
    List<CourseProgress> findByStudentId(@Param("studentId") UUID studentId); // O'quvchining kurs progresslari

    @Query("select cp from CourseProgress cp where cp.course.id = :courseId and cp.studentId = :studentId")
    Optional<CourseProgress> findByCourseIdAndStudentId(@Param("courseId") UUID courseId, @Param("studentId") UUID studentId); // Bitta kurs progressi
}
