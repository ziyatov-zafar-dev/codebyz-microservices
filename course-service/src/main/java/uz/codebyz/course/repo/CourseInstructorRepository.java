package uz.codebyz.course.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.codebyz.course.entity.CourseInstructor;

import java.util.List;
import java.util.UUID;

// Kurs o'qituvchilari uchun repository
public interface CourseInstructorRepository extends JpaRepository<CourseInstructor, UUID> {
    @Query("select ci from CourseInstructor ci where ci.course.id = :courseId")
    List<CourseInstructor> findByCourseId(@Param("courseId") UUID courseId); // Kursdagi o'qituvchilar

    @Query("select ci from CourseInstructor ci where ci.teacherId = :teacherId")
    List<CourseInstructor> findByTeacherId(@Param("teacherId") UUID teacherId); // O'qituvchiga tegishli kurslar
}
