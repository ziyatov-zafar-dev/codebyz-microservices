package uz.codebyz.course.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.codebyz.course.entity.CourseModule;

import java.util.List;
import java.util.UUID;

// Kurs modullari uchun repository
public interface CourseModuleRepository extends JpaRepository<CourseModule, UUID> {
    @Query("select cm from CourseModule cm where cm.course.id = :courseId order by cm.orderIndex")
    List<CourseModule> findByCourseIdOrderByOrderIndex(@Param("courseId") UUID courseId); // Kurs modullari tartibi
}
