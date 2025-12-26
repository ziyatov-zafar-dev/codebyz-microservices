package uz.codebyz.course.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.codebyz.course.entity.AssessmentResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Baholash yakuniy natijalari uchun repository
public interface AssessmentResultRepository extends JpaRepository<AssessmentResult, UUID> {
    @Query("select ar from AssessmentResult ar where ar.assessment.id = :assessmentId")
    List<AssessmentResult> findByAssessmentId(@Param("assessmentId") UUID assessmentId); // Baholash natijalari

    @Query("select ar from AssessmentResult ar where ar.studentId = :studentId")
    List<AssessmentResult> findByStudentId(@Param("studentId") UUID studentId); // O'quvchining natijalari

    @Query("select ar from AssessmentResult ar where ar.assessment.id = :assessmentId and ar.studentId = :studentId")
    Optional<AssessmentResult> findByAssessmentIdAndStudentId(@Param("assessmentId") UUID assessmentId, @Param("studentId") UUID studentId); // Bitta natija
}
