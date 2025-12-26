package uz.codebyz.course.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.codebyz.course.entity.CourseEnrollment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Kursga yozilishlar uchun repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, UUID> {
    @Query("select ce from CourseEnrollment ce where ce.course.id = :courseId")
    List<CourseEnrollment> findByCourseId(@Param("courseId") UUID courseId); // Kursdagi yozilishlar

    @Query("select ce from CourseEnrollment ce where ce.studentId = :studentId")
    List<CourseEnrollment> findByStudentId(@Param("studentId") UUID studentId); // O'quvchi yozilgan kurslar

    @Query("select ce from CourseEnrollment ce where ce.course.id = :courseId and ce.studentId = :studentId")
    Optional<CourseEnrollment> findByCourseIdAndStudentId(@Param("courseId") UUID courseId, @Param("studentId") UUID studentId); // Bitta yozilish
}
