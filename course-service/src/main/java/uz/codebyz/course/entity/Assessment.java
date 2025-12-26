package uz.codebyz.course.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uz.codebyz.course.domain.AssessmentType;

import java.time.LocalDateTime;
import java.util.UUID;

// Kurs ichidagi baholash ta'rifi
@Entity
@Table(name = "assessments")
public class Assessment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; // Asosiy kalit

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course; // Ota kurs

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private AssessmentType type; // Baholash turi

    @Column(nullable = false, length = 255)
    private String title; // Baholash nomi

    @Column(length = 1000)
    private String instructions; // Umumiy ko'rsatmalar

    @Column(nullable = false)
    private int weightPercent = 0; // Yakuniy bahodagi ulushi (%)

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // Yaratilgan vaqt

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public AssessmentType getType() {
        return type;
    }

    public void setType(AssessmentType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public int getWeightPercent() {
        return weightPercent;
    }

    public void setWeightPercent(int weightPercent) {
        this.weightPercent = weightPercent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
