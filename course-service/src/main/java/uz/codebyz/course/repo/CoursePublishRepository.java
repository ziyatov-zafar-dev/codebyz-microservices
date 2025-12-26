package uz.codebyz.course.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.codebyz.course.entity.CoursePublish;

import java.util.List;
import java.util.UUID;

// Kurs nashr holatlari uchun repository
public interface CoursePublishRepository extends JpaRepository<CoursePublish, UUID> {
    @Query("select cp from CoursePublish cp where cp.course.id = :courseId order by cp.changedAt desc")
    List<CoursePublish> findByCourseIdOrderByChangedAtDesc(@Param("courseId") UUID courseId); // Kurs bo'yicha tarix
}
