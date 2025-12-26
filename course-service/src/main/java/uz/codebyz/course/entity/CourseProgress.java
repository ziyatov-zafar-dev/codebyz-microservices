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
import jakarta.persistence.UniqueConstraint;
import uz.codebyz.course.domain.ProgressStatus;

import java.time.LocalDateTime;
import java.util.UUID;

// O'quvchining kurs bo'yicha umumiy progressi
@Entity
@Table(
        name = "course_progress",
        uniqueConstraints = @UniqueConstraint(name = "uk_course_progress_course_student", columnNames = {"course_id", "student_id"})
)
public class CourseProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; // Asosiy kalit

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course; // Maqsad kurs

    @Column(name = "student_id", nullable = false)
    private UUID studentId; // O'quvchi foydalanuvchi identifikatori

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ProgressStatus status = ProgressStatus.NOT_STARTED; // Joriy holat

    @Column
    private LocalDateTime completedAt; // Yakunlangan vaqt

    @Column(nullable = false)
    private int progressPercent = 0; // Progress foizi 0-100

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

    public UUID getStudentId() {
        return studentId;
    }

    public void setStudentId(UUID studentId) {
        this.studentId = studentId;
    }

    public ProgressStatus getStatus() {
        return status;
    }

    public void setStatus(ProgressStatus status) {
        this.status = status;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public int getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(int progressPercent) {
        this.progressPercent = progressPercent;
    }
}
