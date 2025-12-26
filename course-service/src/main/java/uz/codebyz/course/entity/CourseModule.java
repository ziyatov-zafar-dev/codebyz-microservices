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

// Modul kurs ichidagi bo'limni bildiradi
@Entity
@Table(
        name = "course_modules",
        uniqueConstraints = @UniqueConstraint(name = "uk_course_modules_course_order", columnNames = {"course_id", "order_index"})
)
public class CourseModule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; // Asosiy kalit

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course; // Ota kurs

    @Column(nullable = false, length = 255)
    private String title; // Modul sarlavhasi

    @Column(name = "order_index")
    private Integer orderIndex; // Kurs ichidagi tartib

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
