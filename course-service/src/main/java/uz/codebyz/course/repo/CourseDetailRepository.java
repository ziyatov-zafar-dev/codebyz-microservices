package uz.codebyz.course.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.codebyz.course.entity.CourseDetail;

import java.util.Optional;
import java.util.UUID;

// Kursning batafsil ma'lumotlari uchun repository
public interface CourseDetailRepository extends JpaRepository<CourseDetail, UUID> {
    @Query("select cd from CourseDetail cd where cd.course.id = :courseId")
    Optional<CourseDetail> findByCourseId(@Param("courseId") UUID courseId); // Kurs ID bo'yicha topish
}
