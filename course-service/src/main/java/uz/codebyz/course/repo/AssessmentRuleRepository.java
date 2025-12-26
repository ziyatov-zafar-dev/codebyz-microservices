package uz.codebyz.course.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.codebyz.course.entity.AssessmentRule;

import java.util.List;
import java.util.UUID;

// Baholash qoidalari uchun repository
public interface AssessmentRuleRepository extends JpaRepository<AssessmentRule, UUID> {
    @Query("select ar from AssessmentRule ar where ar.assessment.id = :assessmentId")
    List<AssessmentRule> findByAssessmentId(@Param("assessmentId") UUID assessmentId); // Baholash qoidalari
}
