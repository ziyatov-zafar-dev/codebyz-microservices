package uz.codebyz.course.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.codebyz.course.entity.Assessment;

import java.util.List;
import java.util.UUID;

// Baholashlar uchun repository
public interface AssessmentRepository extends JpaRepository<Assessment, UUID> {
    @Query("select a from Assessment a where a.course.id = :courseId")
    List<Assessment> findByCourseId(@Param("courseId") UUID courseId); // Kursdagi baholashlar
}
