package uz.codebyz.course.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.codebyz.course.entity.AssessmentCriteria;

import java.util.List;
import java.util.UUID;

// Baholash mezonlari uchun repository
public interface AssessmentCriteriaRepository extends JpaRepository<AssessmentCriteria, UUID> {
    @Query("select ac from AssessmentCriteria ac where ac.assessment.id = :assessmentId")
    List<AssessmentCriteria> findByAssessmentId(@Param("assessmentId") UUID assessmentId); // Baholash mezonlari
}
