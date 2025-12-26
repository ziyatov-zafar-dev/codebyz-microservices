package uz.codebyz.course.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.codebyz.course.entity.AssessmentScore;

import java.util.List;
import java.util.UUID;

// Baholash ballari uchun repository
public interface AssessmentScoreRepository extends JpaRepository<AssessmentScore, UUID> {
    @Query("select sc from AssessmentScore sc where sc.assessment.id = :assessmentId")
    List<AssessmentScore> findByAssessmentId(@Param("assessmentId") UUID assessmentId); // Baholash bo'yicha ballar

    @Query("select sc from AssessmentScore sc where sc.studentId = :studentId")
    List<AssessmentScore> findByStudentId(@Param("studentId") UUID studentId); // O'quvchining ballari
}
