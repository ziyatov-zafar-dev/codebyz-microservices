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
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;
import java.util.UUID;

// O'quvchining kursga yozilishi yozuvi
@Entity
@Table(
        name = "course_enrollments",
        uniqueConstraints = @UniqueConstraint(name = "uk_course_enrollment_course_student", columnNames = {"course_id", "student_id"})
)
public class CourseEnrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; // Asosiy kalit

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course; // Yozilgan kurs

    @Column(name = "student_id", nullable = false)
    private UUID studentId; // O'quvchi foydalanuvchi identifikatori

    @Column(nullable = false)
    private LocalDateTime enrolledAt = LocalDateTime.now(); // Yozilgan vaqt

    @Column
    private LocalDateTime completedAt; // Yakunlagan vaqt

    @Column(nullable = false)
    private boolean active = true; // Faol yozilish belgisi

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

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
