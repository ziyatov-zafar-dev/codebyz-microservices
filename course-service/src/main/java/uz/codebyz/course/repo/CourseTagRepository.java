package uz.codebyz.course.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.codebyz.course.entity.CourseTag;

import java.util.List;
import java.util.UUID;

// Kurs teglarini boshqarish repositorysi
public interface CourseTagRepository extends JpaRepository<CourseTag, UUID> {
    @Query("select ct from CourseTag ct where ct.course.id = :courseId")
    List<CourseTag> findByCourseId(@Param("courseId") UUID courseId); // Kursdagi teglar

    @Modifying
    @Query("delete from CourseTag ct where ct.course.id = :courseId")
    void deleteByCourseId(@Param("courseId") UUID courseId); // Kurs teglarini o'chirish
}
