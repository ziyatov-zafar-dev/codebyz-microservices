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

import java.time.LocalDateTime;
import java.util.UUID;
import uz.codebyz.course.domain.CourseInstructorRole;

// O'qituvchilarni kurslar bilan roli orqali bog'laydi
@Entity
@Table(
        name = "course_instructors",
        uniqueConstraints = @UniqueConstraint(name = "uk_course_instructor_course_teacher", columnNames = {"course_id", "teacher_id"})
)
public class CourseInstructor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; // Asosiy kalit

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course; // Bog'langan kurs

    @Column(name = "teacher_id", nullable = false)
    private UUID teacherId; // O'qituvchi foydalanuvchi identifikatori

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private CourseInstructorRole role; // Kursdagi roli (enum)

    @Column(nullable = false)
    private LocalDateTime assignedAt = LocalDateTime.now(); // Biriktirilgan vaqt

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

    public UUID getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(UUID teacherId) {
        this.teacherId = teacherId;
    }

    public CourseInstructorRole getRole() {
        return role;
    }

    public void setRole(CourseInstructorRole role) {
        this.role = role;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }
}
