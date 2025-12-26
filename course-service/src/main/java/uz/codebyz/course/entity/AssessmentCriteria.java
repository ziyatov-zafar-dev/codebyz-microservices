package uz.codebyz.course.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;

// Baholashdagi maksimal ball va og'irlikka ega mezon
@Entity
@Table(name = "assessment_criteria")
public class AssessmentCriteria {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; // Asosiy kalit

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment; // Bog'langan baholash

    @Column(nullable = false, length = 255)
    private String label; // Mezon nomi

    @Column(nullable = false)
    private int maxScore; // Ushbu mezon uchun maksimal ball

    @Column(nullable = false)
    private int weightPercent; // Umumiy bahodagi ulushi (%)

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public int getWeightPercent() {
        return weightPercent;
    }

    public void setWeightPercent(int weightPercent) {
        this.weightPercent = weightPercent;
    }
}
