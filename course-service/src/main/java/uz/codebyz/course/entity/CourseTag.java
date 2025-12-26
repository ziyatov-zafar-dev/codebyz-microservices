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

// Kursga biriktirilgan erkin teglar
@Entity
@Table(
        name = "course_tags",
        uniqueConstraints = @UniqueConstraint(name = "uk_course_tags_course_tag", columnNames = {"course_id", "tag"})
)
public class CourseTag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; // Asosiy kalit

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course; // Bog'langan kurs

    @Column(nullable = false, length = 64)
    private String tag; // Teg matni

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // Teg yaratilgan vaqt

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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
